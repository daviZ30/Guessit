package com.dezeta.guessit.domain.entity

import java.io.Serializable

data class User(val email: String, val provider: ProviderType) : Serializable
