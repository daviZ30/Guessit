package com.dezeta.guessit.ui.main

import com.dezeta.guessit.domain.entity.User

sealed class MainState {
    data class UserSuccess(val user: User) : MainState()
    data object SignOut : MainState()
    data class RefreshUser(val user: User) : MainState()
    data class RefreshUrl(val url: String) : MainState()

}