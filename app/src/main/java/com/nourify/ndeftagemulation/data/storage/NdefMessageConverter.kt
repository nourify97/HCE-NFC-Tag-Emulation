package com.nourify.ndeftagemulation.data.storage

import android.nfc.NdefMessage
import androidx.room.TypeConverter

class NdefMessageConverter {
    @TypeConverter
    fun fromNdefMessage(ndefMessage: NdefMessage): ByteArray {
        return ndefMessage.toByteArray()
    }

    @TypeConverter
    fun toNdefMessage(bytes: ByteArray): NdefMessage {
        return NdefMessage(bytes)
    }
}