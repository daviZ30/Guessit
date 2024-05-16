package com.dezeta.guessit.usecase

sealed class LoginState {
    data object emailEmtyError : LoginState()
    data object passwordEmtyError : LoginState()
    data object emailFormatError : LoginState()
    data object passwordFormatError : LoginState()
    data object NotEqualsPasswordError : LoginState()
    data object EmailNotVerifiedError : LoginState()

    data object Success : LoginState()
}