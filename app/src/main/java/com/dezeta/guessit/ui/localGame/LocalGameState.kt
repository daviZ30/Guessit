package com.dezeta.guessit.ui.localGame

sealed class LocalGameState {
    data class DeleteError(var exception: Exception) : LocalGameState()
    data object Success : LocalGameState()

}