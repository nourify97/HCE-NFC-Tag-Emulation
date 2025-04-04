package com.nourify.ndeftagemulation.ui.screens.taglist

import android.content.Context
import android.content.Intent
import android.nfc.NdefMessage
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nourify.ndeftagemulation.data.NdefTagRepo
import com.nourify.ndeftagemulation.data.storage.NdefTag
import com.nourify.ndeftagemulation.service.CardEmulationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class TagListVM(
    private val ndefTagRepo: NdefTagRepo,
) : ViewModel() {
    private val _savedTags = mutableStateListOf<UiTag>()
    val savedTags: List<UiTag> = _savedTags

    init {
        fetchTags()
    }

    private fun fetchTags() {
        viewModelScope.launch(Dispatchers.IO) {
            ndefTagRepo.getAll().collectLatest { list ->
                _savedTags.clear()
                _savedTags.addAll(
                    list.map {
                        UiTag(
                            id = it.id,
                            name = it.name,
                            tagType = it.tagType,
                            isOptionsRevealed = false,
                            ndefMessage = it.ndefMessage,
                        )
                    },
                )
            }
        }
    }

    fun deleteTag(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _savedTags.find { it.id == id }?.let { toRemove ->
                ndefTagRepo.delete(
                    tag =
                        NdefTag(
                            id = toRemove.id,
                            name = toRemove.name,
                            tagType = toRemove.tagType,
                            ndefMessage = toRemove.ndefMessage,
                        ),
                )
            } ?: run {
                Log.e(this.javaClass.name, "Failed to delete tag: ID $id not found")
            }
        }
    }

    fun updateActionVisibility(
        id: Long,
        isVisible: Boolean,
    ) {
        getTagIndexById(id)?.let {
            _savedTags[it] = _savedTags[it].copy(isOptionsRevealed = isVisible)
        }
    }

    fun initTagEmulation(
        tagId: Long,
        ndefMessage: NdefMessage,
        context: Context,
    ): Boolean =
        try {
            context.startService(
                Intent(context, CardEmulationService::class.java).apply {
                    putExtra("ndefMessage", ndefMessage)
                },
            )
            true
        } catch (e: Exception) {
            Log.e(this.javaClass.name, "initTagEmulation: Tag with ID $tagId fail to start HCE service")
            false
        }

    private fun getTagIndexById(id: Long): Int? {
        _savedTags.indexOfFirst { it.id == id }.let { index ->
            if (index == -1) {
                Log.e(this.javaClass.name, "getTagIndexById: Tag with ID $id not found")
                return null
            } else {
                return index
            }
        }
    }
}
