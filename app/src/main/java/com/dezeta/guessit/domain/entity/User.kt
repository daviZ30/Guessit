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
    var completeLevel: Int,
    var countryEnable: Boolean,
    var serieEnable: Boolean,
    var footballEnable: Boolean,
    var statCountry: Int,
    var statSerie: Int,
    var statFootball: Int,
) : Serializable
