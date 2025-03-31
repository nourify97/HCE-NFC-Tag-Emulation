package com.nourify.ndeftagemulation.ui.screen.cardemulation

import android.content.Context
import android.health.connect.datatypes.units.Length
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.nourify.ndeftagemulation.ui.components.NfcCard

@Composable
fun CardEmulation(
    tagInfo: String,
    onTagInfoChange: (String) -> Unit,
    initEmulation: () -> Unit,
    cardEmulationState: CardEmulationState,
    resetCardEmulationState: () -> Unit,
    checkNfcSupport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        NfcCard(tagInfo)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = tagInfo,
            onValueChange = onTagInfoChange,
            label = { Text("Tag content") },
        )
        Button(onClick = initEmulation) {
            Text("Emulate Tag")
        }
    }

    when(cardEmulationState) {
        CardEmulationState.EmptyTextField -> {
            makeToast(context, "The text field is empty")
        }
        CardEmulationState.NfcDisabled -> {
            makeToast(context, "Please activate the NFC")
        }
        CardEmulationState.NoHceSupport -> {
            makeToast(context, "This device doesn't support tag emulation")
        }
        CardEmulationState.HceServiceStartFail -> {
            makeToast(context, "failed to start emulation service")
        }
        CardEmulationState.HceServiceStartSuccess -> {
            makeToast(context, "Started emulation successfully", Toast.LENGTH_LONG)
        }
        else -> {}
    }.let {
        resetCardEmulationState()
    }

    LifecycleResumeEffect(key1 = null) {
        checkNfcSupport()
        onPauseOrDispose {}
    }
}

private fun makeToast(context: Context, msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, msg, duration).show()
}

@Preview
@Composable
private fun CardEmulationPrev() {
    CardEmulation(
        tagInfo = "",
        onTagInfoChange = {},
        initEmulation = {},
        cardEmulationState = CardEmulationState.Idle,
        resetCardEmulationState = {},
        checkNfcSupport = {},
    )
}