package com.nourify.ndeftagemulation.ui.screens.cardemulation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.util.Log
import androidx.lifecycle.ViewModel
import com.nourify.ndeftagemulation.service.HostApduService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CardEmulationVm: ViewModel() {

    private val _tagInfo = MutableStateFlow(TagDetail.toInitialState())
    val tagInfo: StateFlow<TagDetail> = _tagInfo.asStateFlow()

    private val _cardEmulationState = MutableStateFlow<CardEmulationState>(CardEmulationState.Idle)
    val cardEmulationState = _cardEmulationState.asStateFlow()

    fun onMsgTagInfoChange(value: String) {
        _tagInfo.value = _tagInfo.value.copy(tagMsgContent = value)
    }

    fun onWifiTagSsidChange(value: String) {
        _tagInfo.value = _tagInfo.value.copy(
            wifiInfo = _tagInfo.value.wifiInfo.copy(ssid = value)
        )
    }

    fun onWifiTagPasswordChange(value: String) {
        _tagInfo.value = _tagInfo.value.copy(
            wifiInfo = _tagInfo.value.wifiInfo.copy(password = value)
        )
    }

    fun onTagTypeChange(value: Int) {
        _tagInfo.value = _tagInfo.value.copy(tagType = TagType.entries[value])
    }

    fun onVcardTagFirstNameChange(value: String) {
        _tagInfo.value = _tagInfo.value.copy(
            vCardInfo = _tagInfo.value.vCardInfo.copy(firstName = value)
        )
    }

    fun onVcardTagLastNameChange(value: String) {
        _tagInfo.value = _tagInfo.value.copy(
            vCardInfo = _tagInfo.value.vCardInfo.copy(lastName = value)
        )
    }

    fun onVcardTagPhoneNumberChange(value: String) {
        _tagInfo.value = _tagInfo.value.copy(
            vCardInfo = _tagInfo.value.vCardInfo.copy(phoneNumber = value)
        )
    }

    fun onVcardTagEmailChange(value: String) {
        _tagInfo.value = _tagInfo.value.copy(
            vCardInfo = _tagInfo.value.vCardInfo.copy(email = value)
        )
    }

    fun initTagEmulation(activity: Activity, nfcAdapter: NfcAdapter?) {
        val intent = Intent(activity, HostApduService::class.java)

        if (checkNfcSupport(activity, nfcAdapter)) {
            when(_tagInfo.value.tagType) {
                TagType.TEXT_TAG -> {
                    Log.d(this.javaClass.name, "text")

                    if (_tagInfo.value.tagMsgContent.isBlank()) {
                        _cardEmulationState.value = CardEmulationState.EmptyTextField
                    } else {
                        intent.putExtra("ndefMessage", _tagInfo.value.tagMsgContent)
                    }
                }
                TagType.URL_TAG -> {
                    if (_tagInfo.value.tagUrlContent.isBlank()) {
                        // TODO process URL validity
                        _cardEmulationState.value = CardEmulationState.EmptyTextField
                    } else {
                        intent.putExtra("ndefUrl", _tagInfo.value.tagUrlContent)
                    }
                }
                TagType.WIFI_TAG -> {
                    if (_tagInfo.value.wifiInfo.ssid.isBlank() || _tagInfo.value.wifiInfo.password.isBlank()) {
                        _cardEmulationState.value = CardEmulationState.EmptyTextField
                    } else {
                        intent.putExtra("ndefWifi", Json.encodeToString(_tagInfo.value.wifiInfo))
                    }
                }
                TagType.VCARD_TAG -> {
                    if (_tagInfo.value.vCardInfo.firstName.isBlank() || _tagInfo.value.vCardInfo.phoneNumber.isBlank()) {
                        _cardEmulationState.value = CardEmulationState.EmptyTextField
                    } else {
                        intent.putExtra("ndefVcard", Json.encodeToString(_tagInfo.value.vCardInfo))
                    }
                }
            }

            try {
                activity.startService(intent)
                // emulation started successfully
                _cardEmulationState.value = CardEmulationState.HceServiceStartSuccess
            } catch (e: Exception) {
                _cardEmulationState.value = CardEmulationState.HceServiceStartFail
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