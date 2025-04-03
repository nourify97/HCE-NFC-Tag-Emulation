package com.nourify.ndeftagemulation.ui.screens.cardemulation

import android.content.Context
import android.nfc.NfcAdapter
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.nourify.ndeftagemulation.ui.components.NfcCard
import com.nourify.ndeftagemulation.ui.components.TagTypeItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun CardEmulation(
    context: Context,
    mNfcAdapter: NfcAdapter?,
    modifier: Modifier = Modifier,
    vm: CardEmulationVm = koinViewModel()
) {
    val tagInfo by vm.tagInfo.collectAsState()
    val emulationState by vm.cardEmulationState.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        NfcCard(tagInfo.tagMsgContent)
        Spacer(Modifier.height(12.dp))
        TagTypeItem(
            expanded = expanded,
            selectedTagType = tagInfo.tagType.ordinal,
            onExpanded = { expanded = it },
            onSelectedTagType = vm::onTagTypeChange,
        )
        when(tagInfo.tagType) {
            TagType.TEXT_TAG -> {
                OutlinedTextField(
                    value = tagInfo.tagMsgContent,
                    onValueChange = vm::onMsgTagInfoChange,
                    label = { Text("Tag content") },
                )
            }
            TagType.URL_TAG -> {
                OutlinedTextField(
                    value = tagInfo.tagMsgContent,
                    onValueChange = vm::onMsgTagInfoChange,
                    label = { Text("Tag url") },
                )
            }
            TagType.WIFI_TAG -> {
                OutlinedTextField(
                    value = tagInfo.wifiInfo.ssid,
                    onValueChange = vm::onWifiTagSsidChange,
                    label = { Text("Wifi ssid") },
                )
                OutlinedTextField(
                    value = tagInfo.wifiInfo.password,
                    onValueChange = vm::onWifiTagPasswordChange,
                    label = { Text("Wifi password") },
                )
            }
            TagType.VCARD_TAG -> {
                OutlinedTextField(
                    value = tagInfo.vCardInfo.firstName,
                    onValueChange = vm::onVcardTagFirstNameChange,
                    label = { Text("vcard firstname") },
                )
                OutlinedTextField(
                    value = tagInfo.vCardInfo.lastName,
                    onValueChange = vm::onVcardTagLastNameChange,
                    label = { Text("Tag lastname") },
                )
                OutlinedTextField(
                    value = tagInfo.vCardInfo.phoneNumber,
                    onValueChange = vm::onVcardTagPhoneNumberChange,
                    label = { Text("vcard phoneNumber") },
                )
                OutlinedTextField(
                    value = tagInfo.vCardInfo.email,
                    onValueChange = vm::onVcardTagEmailChange,
                    label = { Text("vcard email") },
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                16.dp, Alignment.CenterHorizontally
            )
        ) {
            Button(onClick = { vm.initTagEmulation(context, mNfcAdapter) }) {
                Text("Emulate Tag")
            }
            Button(onClick = vm::saveTag) {
                Text("Save Tag")
            }
        }
        Spacer(Modifier.height(16.dp))
    }

    when(emulationState) {
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

private fun makeToast(context: Context, msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, msg, duration).show()
}