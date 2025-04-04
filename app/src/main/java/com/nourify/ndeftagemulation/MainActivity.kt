package com.nourify.ndeftagemulation

import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nourify.ndeftagemulation.ui.navigation.Setup
import com.nourify.ndeftagemulation.ui.theme.NdefTagEmulationTheme

class MainActivity : ComponentActivity() {
    private var mNfcAdapter: NfcAdapter? = null
    private lateinit var mainVM: MainVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainVM = MainVM()
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)

        enableEdgeToEdge()
        setContent {
            val currentEmulatedTagId by mainVM.currentEmulatedTagId.collectAsStateWithLifecycle()

            NdefTagEmulationTheme {
                Setup(
                    mNfcAdapter = mNfcAdapter,
                    applicationContext = applicationContext,
                    currentEmulatedTagId = currentEmulatedTagId,
                    resetCurrentTag = { mainVM.updateTagId(null) },
                    updateCurrentEmulatedTag = mainVM::updateTagId,
                )
            }
        }
    }
}
