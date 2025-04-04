package com.nourify.ndeftagemulation

import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nourify.ndeftagemulation.ui.navigation.Setup
import com.nourify.ndeftagemulation.ui.theme.NdefTagEmulationTheme

class MainActivity : ComponentActivity() {
    private var mNfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)

        enableEdgeToEdge()
        setContent {
            NdefTagEmulationTheme {
                Setup(
                    mNfcAdapter = mNfcAdapter,
                    applicationContext = applicationContext,
                )
            }
        }
    }
}
