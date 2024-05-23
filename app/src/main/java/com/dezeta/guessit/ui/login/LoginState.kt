package com.dezeta.guessit.ui.login

import com.dezeta.guessit.domain.entity.User

sealed class LoginState {
    data object emailEmtyError : LoginState()
    data object passwordEmtyError : LoginState()
    data object emailFormatError : LoginState()
    data object passwordFormatError : LoginState()
    data object NotEqualsPasswordError : LoginState()
    data object EmailNotVerifiedError : LoginState()
    data object GoogleSignInError : LoginState()

    data object Success : LoginState()
}