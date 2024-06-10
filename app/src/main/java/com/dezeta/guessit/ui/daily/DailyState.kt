package com.dezeta.guessit.ui.daily

sealed class DailyState {
    data object Success : DailyState()
}