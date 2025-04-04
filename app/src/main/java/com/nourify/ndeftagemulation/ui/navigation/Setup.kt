package com.nourify.ndeftagemulation.ui.navigation

import android.content.Context
import android.nfc.NfcAdapter
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.nourify.ndeftagemulation.ui.screens.cardemulation.CardEmulation
import com.nourify.ndeftagemulation.ui.screens.taglist.TagList

@Composable
fun Setup(
    mNfcAdapter: NfcAdapter?,
    applicationContext: Context,
    currentEmulatedTagId: Long?,
    resetCurrentTag: () -> Unit,
    updateCurrentEmulatedTag: (Long) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = { BottomNavigationBar(navController) },
    ) { innerPadding ->
        val graph =
            navController.createGraph(startDestination = Screen.Home.route) {
                composable(route = Screen.Home.route) {
                    CardEmulation(
                        mNfcAdapter = mNfcAdapter,
                        resetCurrentTag = resetCurrentTag,
                        context = applicationContext,
                    )
                }
                composable(route = Screen.TagList.route) {
                    TagList(
                        context = applicationContext,
                        currentEmulatedTagId = currentEmulatedTagId,
                        updateCurrentEmulatedTag = updateCurrentEmulatedTag,
                    )
                }
            }

        NavHost(
            navController = navController,
            graph = graph,
            modifier = Modifier.padding(innerPadding),
        )
    }
}
