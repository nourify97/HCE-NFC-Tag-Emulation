package com.nourify.ndeftagemulation.ui.screen.cardemulation

sealed class CardEmulationState {
    data object Idle: CardEmulationState()
    data object NfcDisabled: CardEmulationState()
    data object NoHceSupport: CardEmulationState()
    data object EmptyTextField: CardEmulationState()
    data object HceServiceStartFail: CardEmulationState()
    data object HceServiceStartSuccess: CardEmulationState()
}