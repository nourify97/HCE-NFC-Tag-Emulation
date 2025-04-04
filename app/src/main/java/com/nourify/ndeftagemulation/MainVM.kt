package com.nourify.ndeftagemulation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainVM : ViewModel() {
    private val _currentEmulatedTagId = MutableStateFlow<Long?>(null)
    val currentEmulatedTagId = _currentEmulatedTagId.asStateFlow()

    fun updateTagId(newId: Long?) = _currentEmulatedTagId.update { newId }
}
