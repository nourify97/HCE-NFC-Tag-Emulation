package com.nourify.ndeftagemulation.ui.screens.cardemulation

import android.content.Context
import android.nfc.NfcAdapter
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.nourify.ndeftagemulation.ui.components.NfcCard
import com.nourify.ndeftagemulation.ui.components.TagTypeItem
import com.nourify.ndeftagemulation.ui.components.TextTagField
import com.nourify.ndeftagemulation.ui.components.UrlTagField
import com.nourify.ndeftagemulation.ui.components.VCardTagField
import com.nourify.ndeftagemulation.ui.components.WifiTagField
import org.koin.androidx.compose.koinViewModel

@Composable
fun CardEmulation(
    context: Context,
    mNfcAdapter: NfcAdapter?,
    resetCurrentTag: () -> Unit,
    modifier: Modifier = Modifier,
    vm: CardEmulationVm = koinViewModel(),
) {
    val tagInfo by vm.tagInfo.collectAsState()
    val emulationState by vm.cardEmulationState.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    ) {
        NfcCard(tagInfo.tagMsgContent)
        Spacer(Modifier.height(12.dp))
        TagTypeItem(
            expanded = expanded,
            selectedTagType = tagInfo.tagType.ordinal,
            onExpanded = { expanded = it },
            onSelectedTagType = vm::onTagTypeChange,
        )
        when (tagInfo.tagType) {
            TagType.TEXT_TAG -> {
                TextTagField(
                    value = tagInfo.tagMsgContent,
                    onValueChange = vm::onMsgTagInfoChange,
                    keyboardController = keyboardController,
                )
            }
            TagType.URL_TAG -> {
                UrlTagField(
                    value = tagInfo.tagMsgContent,
                    onValueChange = vm::onUrlTagInfoChange,
                    keyboardController = keyboardController,
                )
            }
            TagType.WIFI_TAG -> {
                WifiTagField(
                    ssidValue = tagInfo.wifiInfo.ssid,
                    passValue = tagInfo.wifiInfo.password,
                    onSsidValueChange = vm::onWifiTagSsidChange,
                    onPassValueChange = vm::onWifiTagPasswordChange,
                    keyboardController = keyboardController,
                )
            }
            TagType.VCARD_TAG -> {
                VCardTagField(
                    firstname = tagInfo.vCardInfo.firstName,
                    lastname = tagInfo.vCardInfo.lastName,
                    phoneNumber = tagInfo.vCardInfo.phoneNumber,
                    email = tagInfo.vCardInfo.email,
                    onFirstnameValueChange = vm::onVcardTagFirstNameChange,
                    onLastnameValueChange = vm::onVcardTagLastNameChange,
                    onPhoneNumberValueChange = vm::onVcardTagPhoneNumberChange,
                    onEmailValueChange = vm::onVcardTagEmailChange,
                    keyboardController = keyboardController,
                )
            }
        }
        Row(
            horizontalArrangement =
                Arrangement.spacedBy(
                    16.dp,
                    Alignment.CenterHorizontally,
                ),
        ) {
            Button(onClick = {
                vm.initTagEmulation(context, mNfcAdapter)
                resetCurrentTag()
            }) {
                Text("Emulate Tag")
            }
            Button(onClick = {
                vm.saveTag()
            }) {
                Text("Save Tag")
            }
        }
        Spacer(Modifier.height(16.dp))
    }

    when (emulationState) {
        CardEmulationState.EmptyTextField -> makeToast(context, "The text field is empty")
        CardEmulationState.NfcDisabled -> makeToast(context, "Please activate the NFC")
        CardEmulationState.NoHceSupport -> makeToast(context, "This device doesn't support tag emulation")
        CardEmulationState.HceServiceStartFail -> makeToast(context, "failed to start emulation service")
        CardEmulationState.HceServiceStartSuccess -> makeToast(context, "Started emulation successfully", Toast.LENGTH_LONG)
        else -> {}
    }.let {
        vm.resetCardEmulationStat()
    }

    LifecycleResumeEffect(key1 = null) {
        vm.checkNfcSupport(context, mNfcAdapter)
        onPauseOrDispose {}
    }
}

private fun makeToast(
    context: Context,
    msg: String,
    duration: Int = Toast.LENGTH_SHORT,
) {
    Toast.makeText(context, msg, duration).show()
}
