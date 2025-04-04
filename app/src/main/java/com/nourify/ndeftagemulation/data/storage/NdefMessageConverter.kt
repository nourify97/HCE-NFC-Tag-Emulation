package com.nourify.ndeftagemulation.data.storage

import android.nfc.NdefMessage
import androidx.room.TypeConverter

class NdefMessageConverter {
    @TypeConverter
    fun fromNdefMessage(ndefMessage: NdefMessage): ByteArray = ndefMessage.toByteArray()

    @TypeConverter
    fun toNdefMessage(bytes: ByteArray): NdefMessage = NdefMessage(bytes)
}
