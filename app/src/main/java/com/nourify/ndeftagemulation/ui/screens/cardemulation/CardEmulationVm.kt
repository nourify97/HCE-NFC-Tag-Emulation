package com.nourify.ndeftagemulation.ui.screens.cardemulation

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nourify.ndeftagemulation.data.NdefTagRepo
import com.nourify.ndeftagemulation.data.storage.NdefTag
import com.nourify.ndeftagemulation.service.CardEmulationService
import com.nourify.ndeftagemulation.util.NdefEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class CardEmulationVm(
    private val ndefEncoder: NdefEncoder,
    private val ndefTagRepo: NdefTagRepo,
) : ViewModel() {
    private val _tagInfo = MutableStateFlow(TagDetail.toInitialState())
    val tagInfo: StateFlow<TagDetail> = _tagInfo.asStateFlow()

    private val _cardEmulationState = MutableStateFlow<CardEmulationState>(CardEmulationState.Idle)
    val cardEmulationState = _cardEmulationState.asStateFlow()

    fun onMsgTagInfoChange(value: String) = _tagInfo.update { it.copy(tagMsgContent = value) }

    fun onUrlTagInfoChange(value: String) = _tagInfo.update { it.copy(tagUrlContent = value) }

    fun onWifiTagSsidChange(value: String) = _tagInfo.update { it.copy(wifiInfo = it.wifiInfo.copy(ssid = value)) }

    fun onWifiTagPasswordChange(value: String) = _tagInfo.update { it.copy(wifiInfo = it.wifiInfo.copy(password = value)) }

    fun onTagTypeChange(value: Int) = _tagInfo.update { it.copy(tagType = TagType.entries[value]) }

    fun onVcardTagFirstNameChange(value: String) = _tagInfo.update { it.copy(vCardInfo = it.vCardInfo.copy(firstName = value)) }

    fun onVcardTagLastNameChange(value: String) = _tagInfo.update { it.copy(vCardInfo = it.vCardInfo.copy(lastName = value)) }

    fun onVcardTagPhoneNumberChange(value: String) = _tagInfo.update { it.copy(vCardInfo = it.vCardInfo.copy(phoneNumber = value)) }

    fun onVcardTagEmailChange(value: String) = _tagInfo.update { it.copy(vCardInfo = it.vCardInfo.copy(email = value)) }

    fun initTagEmulation(
        context: Context,
        nfcAdapter: NfcAdapter?,
    ) {
        var ndefMessage: NdefMessage? = null

        if (checkNfcSupport(context, nfcAdapter)) {
            when (_tagInfo.value.tagType) {
                TagType.TEXT_TAG -> {
                    if (_tagInfo.value.tagMsgContent.isBlank()) {
                        _cardEmulationState.value = CardEmulationState.EmptyTextField
                    } else {
                        ndefMessage = ndefEncoder.encodeText(_tagInfo.value.tagMsgContent)
                    }
                }
                TagType.URL_TAG -> {
                    if (isUrlValid(_tagInfo.value.tagUrlContent)) {
                        ndefMessage = ndefEncoder.encodeUrl(_tagInfo.value.tagUrlContent)
                    } else {
                        _cardEmulationState.value = CardEmulationState.InvalidURL
                    }
                }
                TagType.WIFI_TAG -> {
                    if (_tagInfo.value.wifiInfo.ssid
                            .isBlank() ||
                        _tagInfo.value.wifiInfo.password
                            .isBlank()
                    ) {
                        _cardEmulationState.value = CardEmulationState.EmptyTextField
                    } else {
                        ndefMessage = ndefEncoder.encodeWifi(_tagInfo.value.wifiInfo)
                    }
                }
                TagType.VCARD_TAG -> {
                    if (_tagInfo.value.vCardInfo.firstName
                            .isBlank() ||
                        _tagInfo.value.vCardInfo.phoneNumber
                            .isBlank()
                    ) {
                        _cardEmulationState.value = CardEmulationState.EmptyTextField
                    } else {
                        ndefMessage = ndefEncoder.encodeVcard(_tagInfo.value.vCardInfo)
                    }
                }
            }

            try {
                ndefMessage?.let { msg ->
                    context.startService(
                        Intent(context, CardEmulationService::class.java).apply {
                            putExtra("ndefMessage", msg)
                        },
                    )
                } ?: run {
                    Log.i(this.javaClass.name, "NdefMessage is Null")
                    return
                }

                // emulation started successfully
                _cardEmulationState.value = CardEmulationState.HceServiceStartSuccess
            } catch (e: Exception) {
                Log.e(this.javaClass.name, "Failed to start HCE service", e)
                _cardEmulationState.value = CardEmulationState.HceServiceStartFail
            }
        }
    }

    fun saveTag() {
        viewModelScope.launch(Dispatchers.IO) {
            when (_tagInfo.value.tagType) {
                TagType.TEXT_TAG -> {
                    if (_tagInfo.value.tagMsgContent.isBlank()) {
                        _cardEmulationState.value = CardEmulationState.EmptyTextField
                    } else {
                        ndefTagRepo.insert(
                            tag =
                                NdefTag(
                                    tagType = TagType.TEXT_TAG,
                                    name = _tagInfo.value.tagMsgContent,
                                    ndefMessage = ndefEncoder.encodeText(_tagInfo.value.tagMsgContent),
                                ),
                        )
                    }
                }
                TagType.URL_TAG -> {
                    if (isUrlValid(_tagInfo.value.tagUrlContent)) {
                        ndefTagRepo.insert(
                            tag =
                                NdefTag(
                                    tagType = TagType.URL_TAG,
                                    name = _tagInfo.value.tagUrlContent,
                                    ndefMessage = ndefEncoder.encodeUrl(_tagInfo.value.tagUrlContent),
                                ),
                        )
                    } else {
                        _cardEmulationState.value = CardEmulationState.InvalidURL
                    }
                }
                TagType.WIFI_TAG -> {
                    if (_tagInfo.value.wifiInfo.ssid
                            .isBlank() ||
                        _tagInfo.value.wifiInfo.password
                            .isBlank()
                    ) {
                        _cardEmulationState.value = CardEmulationState.EmptyTextField
                    } else {
                        ndefTagRepo.insert(
                            tag =
                                NdefTag(
                                    tagType = TagType.WIFI_TAG,
                                    name = _tagInfo.value.wifiInfo.ssid,
                                    ndefMessage = ndefEncoder.encodeWifi(_tagInfo.value.wifiInfo),
                                ),
                        )
                    }
                }
                TagType.VCARD_TAG -> {
                    if (_tagInfo.value.vCardInfo.firstName
                            .isBlank() ||
                        _tagInfo.value.vCardInfo.phoneNumber
                            .isBlank()
                    ) {
                        _cardEmulationState.value = CardEmulationState.EmptyTextField
                    } else {
                        ndefTagRepo.insert(
                            tag =
                                NdefTag(
                                    tagType = TagType.VCARD_TAG,
                                    name = _tagInfo.value.vCardInfo.firstName,
                                    ndefMessage = ndefEncoder.encodeVcard(_tagInfo.value.vCardInfo),
                                ),
                        )
                    }
                }
            }
        }
    }

    private fun isUrlValid(url: String) =
        url.isNotBlank() &&
            url.startsWith("https://", ignoreCase = true) &&
            Patterns.WEB_URL.matcher(url).matches()

    fun checkNfcSupport(
        context: Context,
        nfcAdapter: NfcAdapter?,
    ): Boolean =
        when {
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

    fun resetCardEmulationStat() = _cardEmulationState.run { value = CardEmulationState.Idle }
}
