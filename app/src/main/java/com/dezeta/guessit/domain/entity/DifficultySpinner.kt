package com.dezeta.guessit.domain.entity

data class DifficultySpinner(val value: String, val key: String) {
    override fun toString(): String {
        return value
    }
}