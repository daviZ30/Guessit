package com.dezeta.guessit.domain.entity

import java.io.Serializable

data class User(val email: String, var point: Int, val provider: ProviderType, var level: Int) :
    Serializable
