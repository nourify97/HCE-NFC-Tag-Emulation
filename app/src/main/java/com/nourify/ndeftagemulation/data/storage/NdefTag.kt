package com.nourify.ndeftagemulation.data.storage

import android.nfc.NdefMessage
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nourify.ndeftagemulation.ui.screens.cardemulation.TagType

@Entity(tableName = "ndefTags")
data class NdefTag(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val tagType: TagType,
    val ndefMessage: NdefMessage,
)
