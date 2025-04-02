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
import com.nourify.ndeftagemulation.ui.screens.cardemulation.CardEmulation
import com.nourify.ndeftagemulation.ui.screens.cardemulation.CardEmulationVm
import com.nourify.ndeftagemulation.ui.theme.NdefTagEmulationTheme

class MainActivity : ComponentActivity() {

    private lateinit var vm: CardEmulationVm
    private var mNfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm = CardEmulationVm()
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)

        enableEdgeToEdge()
        setContent {
            NdefTagEmulationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CardEmulation(
                        vm = vm,
                        initEmulation = { vm.initTagEmulation(applicationContext, mNfcAdapter) },
                        checkNfcSupport = { vm.checkNfcSupport(applicationContext, mNfcAdapter) },
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}