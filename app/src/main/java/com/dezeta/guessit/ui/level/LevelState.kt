package com.dezeta.guessit.ui.level

import com.dezeta.guessit.domain.Repository.Resource

sealed class LevelState {
    data class Success<T>(var data: T?) : LevelState()
}