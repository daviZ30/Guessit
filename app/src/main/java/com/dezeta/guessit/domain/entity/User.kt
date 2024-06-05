package com.dezeta.guessit.domain.entity

import java.io.Serializable

data class User(
    val email: String,
    var name: String,
    val friends: List<String>,
    var point: Int,
    val provider: ProviderType,
    var level: Int,
    var img_url: String,
    var completeLevel: Int
) :
    Serializable
