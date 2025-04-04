package com.nourify.ndeftagemulation.ui.screens.taglist

import android.nfc.NdefMessage
import com.nourify.ndeftagemulation.ui.screens.cardemulation.TagType

data class UiTag(
    val id: Long,
    val name: String,
    val tagType: TagType,
    val isOptionsRevealed: Boolean,
    val ndefMessage: NdefMessage,
)
