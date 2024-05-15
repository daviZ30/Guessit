package com.dezeta.guessit.domain.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.dezeta.guessit.domain.entity.Difficulty

@ProvidedTypeConverter
class DifficultyConverter {
    @TypeConverter
    fun toDifficulty(value: String): Difficulty {
        return Difficulty.valueOf(value)
    }

    @TypeConverter
    fun fromDifficulty(status : Difficulty):String{
        return status.toString()
    }
}