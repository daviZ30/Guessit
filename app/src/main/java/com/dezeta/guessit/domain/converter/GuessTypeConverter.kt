package com.dezeta.guessit.domain.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.dezeta.guessit.domain.entity.Category
import com.dezeta.guessit.domain.entity.Difficulty
import com.dezeta.guessit.domain.entity.GuessType

@ProvidedTypeConverter
class GuessTypeConverter {
    @TypeConverter
    fun toGuessType(value: String): GuessType {
        return GuessType.valueOf(value)
    }

    @TypeConverter
    fun fromGuessType(gt: GuessType): String {
        return gt.toString()
    }
}