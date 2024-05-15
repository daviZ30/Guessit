package com.dezeta.guessit.domain.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.dezeta.guessit.domain.entity.Category
import com.dezeta.guessit.domain.entity.Difficulty

@ProvidedTypeConverter
class CategoryConverter {
    @TypeConverter
    fun toCategory(value: String): Category {
        return Category.valueOf(value)
    }

    @TypeConverter
    fun fromCategory(c: Category): String {
        return c.toString()
    }
}