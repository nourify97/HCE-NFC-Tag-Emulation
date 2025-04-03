package com.nourify.ndeftagemulation.service

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.cardemulation.HostApduService
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.nourify.ndeftagemulation.data.NdefEncoder
import com.nourify.ndeftagemulation.util.fillByteArrayToFixedDimension
import com.nourify.ndeftagemulation.util.toHex
import org.koin.android.ext.android.inject
import java.math.BigInteger

/**
 * This class emulates a NFC Forum Tag Type 4 containing a NDEF message
 * The class uses the AID D2760000850101
 */
class CardEmulationService : HostApduService() {

    private val ndefEncoder by inject<NdefEncoder>()

    // In the scenario that we have done a CC read, the same byte[] match
    // for ReadBinary would trigger and we don't want that in succession
    private var readCapabilityContainerCheck = false

    private var ndefUri = ndefEncoder.encodeText("Ciao, come va?")
    private var ndefUriBytes = ndefUri.toByteArray()
    private var ndefUriLen = fillByteArrayToFixedDimension(
        BigInteger.valueOf(ndefUriBytes.size.toLong()).toByteArray(),
        2,
    )

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.hasExtra("ndefMessage") == true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("ndefMessage", NdefMessage::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra("ndefMessage") as NdefMessage?
            }?.let {
                ndefUriBytes = it.toByteArray()
                ndefUriLen = fillByteArrayToFixedDimension(
                    BigInteger.valueOf(ndefUriBytes.size.toLong()).toByteArray(),
                    2,
                )

                Log.i(TAG, "onStartCommand() | NDEF$it")
            } ?: run {
                Log.e("HostApduService", "the ndef message is Null")
            }
        }

        return START_STICKY
    }

    override fun processCommandApdu(commandApdu: ByteArray, extras: Bundle?): ByteArray {
        //
        // The following flow is based on Appendix E "Example of Mapping Version 2.0 Command Flow"
        // in the NFC Forum specification
        //
        Log.i(TAG, "processCommandApdu() | incoming commandApdu: " + commandApdu.toHex())

        //
        // First command: NDEF Tag Application select (Section 5.5.2 in NFC Forum spec)
        //
        if (APDU_SELECT.contentEquals(commandApdu)) {
            Log.i(TAG, "APDU_SELECT triggered. Our Response: " + A_OKAY.toHex())
            return A_OKAY
        }

        //
        // Second command: Capability Container select (Section 5.5.3 in NFC Forum spec)
        //
        if (CAPABILITY_CONTAINER_OK.contentEquals(commandApdu)) {
            Log.i(TAG, "CAPABILITY_CONTAINER_OK triggered. Our Response: " + A_OKAY.toHex())
            return A_OKAY
        }

        //
        // Third command: ReadBinary data from CC file (Section 5.5.4 in NFC Forum spec)
        //
        if (READ_CAPABILITY_CONTAINER.contentEquals(commandApdu) && !readCapabilityContainerCheck
        ) {
            Log.i(
                TAG,
                "READ_CAPABILITY_CONTAINER triggered. Our Response: " + READ_CAPABILITY_CONTAINER_RESPONSE.toHex(),
            )
            readCapabilityContainerCheck = true
            return READ_CAPABILITY_CONTAINER_RESPONSE
        }

        //
        // Fourth command: NDEF Select command (Section 5.5.5 in NFC Forum spec)
        //
        if (NDEF_SELECT_OK.contentEquals(commandApdu)) {
            Log.i(TAG, "NDEF_SELECT_OK triggered. Our Response: " + A_OKAY.toHex())
            return A_OKAY
        }

        if (NDEF_READ_BINARY_NLEN.contentEquals(commandApdu)) {
            // Build our response
            val response = ByteArray(ndefUriLen.size + A_OKAY.size)
            System.arraycopy(ndefUriLen, 0, response, 0, ndefUriLen.size)
            System.arraycopy(A_OKAY, 0, response, ndefUriLen.size, A_OKAY.size)

            Log.i(TAG, "NDEF_READ_BINARY_NLEN triggered. Our Response: " + response.toHex())

            readCapabilityContainerCheck = false
            return response
        }

        if (commandApdu.sliceArray(0..1).contentEquals(NDEF_READ_BINARY)) {
            val offset = commandApdu.sliceArray(2..3).toHex().toInt(16)
            val length = commandApdu.sliceArray(4..4).toHex().toInt(16)

            val fullResponse = ByteArray(ndefUriLen.size + ndefUriBytes.size)
            System.arraycopy(ndefUriLen, 0, fullResponse, 0, ndefUriLen.size)
            System.arraycopy(
                ndefUriBytes,
                0,
                fullResponse,
                ndefUriLen.size,
                ndefUriBytes.size,
            )

            Log.i(TAG, "NDEF_READ_BINARY triggered. Full data: " + fullResponse.toHex())
            Log.i(TAG, "READ_BINARY - OFFSET: $offset - LEN: $length")

            val slicedResponse = fullResponse.sliceArray(offset until fullResponse.size)

            // Build our response
            val realLength = if (slicedResponse.size <= length) slicedResponse.size else length
            val response = ByteArray(realLength + A_OKAY.size)

            System.arraycopy(slicedResponse, 0, response, 0, realLength)
            System.arraycopy(A_OKAY, 0, response, realLength, A_OKAY.size)

            Log.i(TAG, "NDEF_READ_BINARY triggered. Our Response: " + response.toHex())

            readCapabilityContainerCheck = false
            return response
        }

        //
        // We're doing something outside our scope
        //
        Log.wtf(TAG, "processCommandApdu() | I don't know what's going on!!!")
        return A_ERROR
    }

    override fun onDeactivated(reason: Int) {
        Log.i(TAG, "onDeactivated() Fired! Reason: $reason")
    }

    companion object {
        private const val TAG = "HostApduService"

        private val APDU_SELECT = byteArrayOf(
            0x00.toByte(), // CLA	- Class - Class of instruction
            0xA4.toByte(), // INS	- Instruction - Instruction code
            0x04.toByte(), // P1	- Parameter 1 - Instruction parameter 1
            0x00.toByte(), // P2	- Parameter 2 - Instruction parameter 2
            0x07.toByte(), // Lc field	- Number of bytes present in the data field of the command
            0xD2.toByte(),
            0x76.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x85.toByte(),
            0x01.toByte(),
            0x01.toByte(), // NDEF Tag Application name
            0x00.toByte(), // Le field	- Maximum number of bytes expected in the data field of the response to the command
        )

        private val CAPABILITY_CONTAINER_OK = byteArrayOf(
            0x00.toByte(), // CLA	- Class - Class of instruction
            0xa4.toByte(), // INS	- Instruction - Instruction code
            0x00.toByte(), // P1	- Parameter 1 - Instruction parameter 1
            0x0c.toByte(), // P2	- Parameter 2 - Instruction parameter 2
            0x02.toByte(), // Lc field	- Number of bytes present in the data field of the command
            0xe1.toByte(),
            0x03.toByte(), // file identifier of the CC file
        )

        private val READ_CAPABILITY_CONTAINER = byteArrayOf(
            0x00.toByte(), // CLA	- Class - Class of instruction
            0xb0.toByte(), // INS	- Instruction - Instruction code
            0x00.toByte(), // P1	- Parameter 1 - Instruction parameter 1
            0x00.toByte(), // P2	- Parameter 2 - Instruction parameter 2
            0x0f.toByte(), // Lc field	- Number of bytes present in the data field of the command
        )

        private val READ_CAPABILITY_CONTAINER_RESPONSE = byteArrayOf(
            0x00.toByte(), 0x0F.toByte(), // CCLEN length of the CC file
            0x20.toByte(), // Mapping Version 2.0
            0x00.toByte(), 0x3B.toByte(), // MLe maximum
            0x00.toByte(), 0x34.toByte(), // MLc maximum
            0x04.toByte(), // T field of the NDEF File Control TLV
            0x06.toByte(), // L field of the NDEF File Control TLV
            0xE1.toByte(), 0x04.toByte(), // File Identifier of NDEF file
            0x00.toByte(), 0xFF.toByte(), // Maximum NDEF file size of 65534 bytes
            0x00.toByte(), // Read access without any security
            0xFF.toByte(), // Write access without any security
            0x90.toByte(), 0x00.toByte(), // A_OKAY
        )

        private val NDEF_SELECT_OK = byteArrayOf(
            0x00.toByte(), // CLA	- Class - Class of instruction
            0xa4.toByte(), // Instruction byte (INS) for Select command
            0x00.toByte(), // Parameter byte (P1), select by identifier
            0x0c.toByte(), // Parameter byte (P1), select by identifier
            0x02.toByte(), // Lc field	- Number of bytes present in the data field of the command
            0xE1.toByte(),
            0x04.toByte(), // file identifier of the NDEF file retrieved from the CC file
        )

        private val NDEF_READ_BINARY = byteArrayOf(
            0x00.toByte(), // Class byte (CLA)
            0xb0.toByte(), // Instruction byte (INS) for ReadBinary command
        )

        private val NDEF_READ_BINARY_NLEN = byteArrayOf(
            0x00.toByte(), // Class byte (CLA)
            0xb0.toByte(), // Instruction byte (INS) for ReadBinary command
            0x00.toByte(),
            0x00.toByte(), // Parameter byte (P1, P2), offset inside the CC file
            0x02.toByte(), // Le field
        )

        private val A_OKAY = byteArrayOf(
            0x90.toByte(), // SW1	Status byte 1 - Command processing status
            0x00.toByte(), // SW2	Status byte 2 - Command processing qualifier
        )

        private val A_ERROR = byteArrayOf(
            0x6A.toByte(), // SW1	Status byte 1 - Command processing status
            0x82.toByte(), // SW2	Status byte 2 - Command processing qualifier
        )
    }
}
