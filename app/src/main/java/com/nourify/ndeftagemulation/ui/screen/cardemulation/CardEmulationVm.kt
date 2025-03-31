package com.nourify.ndeftagemulation.ui.screen.cardemulation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import androidx.lifecycle.ViewModel
import com.nourify.ndeftagemulation.service.HostApduService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CardEmulationVm: ViewModel() {

    private val _tagInfo = MutableStateFlow("")
    val tagInfo = _tagInfo.asStateFlow()

    private val _cardEmulationState = MutableStateFlow<CardEmulationState>(CardEmulationState.Idle)
    val cardEmulationState = _cardEmulationState.asStateFlow()

    fun onTagInfoChange(value: String) {
        _tagInfo.value = value
    }

    fun initTagEmulation(activity: Activity, nfcAdapter: NfcAdapter?) {
        if (checkNfcSupport(activity, nfcAdapter)) {
            if (_tagInfo.value.isBlank()) {
                _cardEmulationState.value = CardEmulationState.EmptyTextField
            } else {
                try {
                    val intent = Intent(activity, HostApduService::class.java)
                    intent.putExtra("ndefMessage", _tagInfo.value)
                    activity.startService(intent)
                    // emulation started successfully
                    _cardEmulationState.value = CardEmulationState.HceServiceStartSuccess
                } catch (e: Exception) {
                    _cardEmulationState.value = CardEmulationState.HceServiceStartFail
                }
            }
        }
    }

    fun checkNfcSupport(
        context: Context,
        nfcAdapter: NfcAdapter?,
    ): Boolean {
        return when {
            nfcAdapter?.isEnabled == false -> {
                _cardEmulationState.value = CardEmulationState.NfcDisabled
                false
            }
            !context.packageManager.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION) -> {
                _cardEmulationState.value = CardEmulationState.NoHceSupport
                false
            }
            else -> {
                _cardEmulationState.value = CardEmulationState.Idle
                true
            }
        }
    }

    fun resetCardEmulationStat() = _cardEmulationState.run { value = CardEmulationState.Idle }
}