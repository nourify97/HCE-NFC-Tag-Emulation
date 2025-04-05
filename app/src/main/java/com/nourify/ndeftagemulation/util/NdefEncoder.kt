package com.nourify.ndeftagemulation.util

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import com.nourify.ndeftagemulation.ui.screens.cardemulation.VcardInfo
import com.nourify.ndeftagemulation.ui.screens.cardemulation.WifiInfo
import org.koin.core.annotation.Single
import java.io.UnsupportedEncodingException
import java.nio.ByteBuffer
import java.nio.charset.Charset

@Single
class NdefEncoder {
    fun encodeText(text: String): NdefMessage = NdefMessage(NdefRecord.createTextRecord(DEFAULT_LANGUAGE, text))

    fun encodeUrl(url: String): NdefMessage = NdefMessage(NdefRecord.createUri(url))

    fun encodeWifi(wifiInfo: WifiInfo): NdefMessage {
        val payload = generateNdefPayload(wifiInfo.ssid, wifiInfo.password)

        val mimeRecord =
            NdefRecord(
                NdefRecord.TNF_MIME_MEDIA,
                NFC_TOKEN_MIME_TYPE.toByteArray(Charset.forName(DEFAULT_CHARSET)),
                ByteArray(0),
                payload,
            )

        return NdefMessage(mimeRecord)
    }

    fun encodeVcard(vcardInfo: VcardInfo): NdefMessage =
        createVCardNdefMessage(
            firstName = vcardInfo.firstName,
            lastName = vcardInfo.lastName,
            phoneNumber = vcardInfo.phoneNumber,
            email = vcardInfo.email,
        )

    private fun generateNdefPayload(
        ssid: String,
        key: String,
    ): ByteArray {
        val ssidBytes = ssid.toByteArray()
        val keyBytes = key.toByteArray()

        val bufferSize = 18 + ssidBytes.size + keyBytes.size
        val buffer = ByteBuffer.allocate(bufferSize)

        buffer.putShort(CREDENTIAL_FIELD_ID)
        buffer.putShort((bufferSize - 4).toShort())

        buffer.putShort(SSID_FIELD_ID)
        buffer.putShort(ssidBytes.size.toShort())
        buffer.put(ssidBytes)

        buffer.putShort(AUTH_TYPE_FIELD_ID)
        buffer.putShort(2)
        buffer.putShort(AUTH_TYPE_WPA_PSK) // Or other auth type

        buffer.putShort(NETWORK_KEY_FIELD_ID)
        buffer.putShort(keyBytes.size.toShort())
        buffer.put(keyBytes)

        return buffer.array()
    }

    private fun createTextRecord(
        language: String = DEFAULT_LANGUAGE,
        text: String,
        id: ByteArray,
    ): NdefRecord {
        val languageBytes: ByteArray
        val textBytes: ByteArray
        try {
            languageBytes = language.toByteArray(charset(DEFAULT_CHARSET))
            textBytes = text.toByteArray(charset(DEFAULT_CHARSET_NAME))
        } catch (e: UnsupportedEncodingException) {
            throw AssertionError(e)
        }

        val recordPayload = ByteArray(1 + (languageBytes.size and 0x03F) + textBytes.size)

        recordPayload[0] = (languageBytes.size and 0x03F).toByte()
        System.arraycopy(languageBytes, 0, recordPayload, 1, languageBytes.size and 0x03F)
        System.arraycopy(
            textBytes,
            0,
            recordPayload,
            1 + (languageBytes.size and 0x03F),
            textBytes.size,
        )

        return NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, id, recordPayload)
    }

    private fun createVCardNdefMessage(
        firstName: String,
        lastName: String,
        phoneNumber: String,
        email: String,
    ): NdefMessage {
        val vCardString =
            """
            BEGIN:VCARD
            VERSION:3.0
            N:$lastName;$firstName;;;;
            FN:$firstName $lastName
            TEL;TYPE=cell:$phoneNumber
            EMAIL:$email
            END:VCARD
            """.trimIndent()

        val mimeType = "text/x-vcard"
        val vCardBytes = vCardString.toByteArray(Charset.forName(DEFAULT_CHARSET_NAME))

        val mimeRecord =
            NdefRecord(
                NdefRecord.TNF_MIME_MEDIA,
                mimeType.toByteArray(Charset.forName(DEFAULT_CHARSET)),
                ByteArray(0),
                vCardBytes,
            )

        return NdefMessage(mimeRecord)
    }

    companion object {
        private val NDEF_ID = byteArrayOf(0xE1.toByte(), 0x04.toByte())

        private const val DEFAULT_CHARSET_NAME = "UTF-8"
        private const val DEFAULT_CHARSET = "US-ASCII"
        private const val DEFAULT_LANGUAGE = "en"

        // constants NFC WIFI
        private const val NFC_TOKEN_MIME_TYPE = "application/vnd.wfa.wsc"
        private const val CREDENTIAL_FIELD_ID: Short = 0x100e
        private const val SSID_FIELD_ID: Short = 0x1045
        private const val AUTH_TYPE_FIELD_ID: Short = 0x1003
        private const val AUTH_TYPE_OPEN: Short = 0x0001
        private const val AUTH_TYPE_WPA_PSK: Short = 0x0002
        private const val AUTH_TYPE_WPA2_PSK: Short = 0x0020
        private const val NETWORK_KEY_FIELD_ID: Short = 0x1027
    }
}
