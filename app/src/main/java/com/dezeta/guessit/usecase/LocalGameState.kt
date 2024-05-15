package com.dezeta.guessit.usecase

sealed class LocalGameState {
    data class DeleteError(var exception: Exception) : LocalGameState()
    data object Success : LocalGameState()

}