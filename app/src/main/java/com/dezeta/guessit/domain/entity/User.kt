package com.dezeta.guessit.domain.entity

import java.io.Serializable

data class User(val email: String, val name: String, val point: Int, val provider: ProviderType) :
    Serializable
