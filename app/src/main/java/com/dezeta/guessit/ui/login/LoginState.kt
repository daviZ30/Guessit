package com.dezeta.guessit.ui.login

sealed class LoginState {
    data object emailEmtyError : LoginState()
    data object nameEmtyError : LoginState()
    data object passwordEmtyError : LoginState()
    data object emailFormatError : LoginState()
    data object passwordFormatError : LoginState()
    data object NotEqualsPasswordError : LoginState()
    data object EmailNotVerifiedError : LoginState()

    data object Success : LoginState()
}