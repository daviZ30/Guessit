package com.dezeta.guessit.domain.Repository

sealed class Resource {
    data class Success<T>(var data: T?) :
        Resource()
    data class Error(var exception: Exception):Resource()
}