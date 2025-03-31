package com.nourify.ndeftagemulation

import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.nourify.ndeftagemulation.ui.screen.cardemulation.CardEmulation
import com.nourify.ndeftagemulation.ui.screen.cardemulation.CardEmulationVm
import com.nourify.ndeftagemulation.ui.theme.NdefTagEmulationTheme

class MainActivity : ComponentActivity() {

    lateinit var vm: CardEmulationVm
    private var mNfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm = CardEmulationVm()
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)

        enableEdgeToEdge()
        setContent {
            val tagInfo by vm.tagInfo.collectAsState()
            val emulationState by vm.cardEmulationState.collectAsState()

            NdefTagEmulationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CardEmulation(
                        tagInfo = tagInfo,
                        onTagInfoChange = vm::onTagInfoChange,
                        initEmulation = { vm.initTagEmulation(this, mNfcAdapter) },
                        cardEmulationState = emulationState,
                        resetCardEmulationState = vm::resetCardEmulationStat,
                        checkNfcSupport = { vm.checkNfcSupport(this, mNfcAdapter) },
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}