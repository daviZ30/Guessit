package com.dezeta.guessit.domain.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.time.Instant

@ProvidedTypeConverter
class InstantConverter {

    @TypeConverter
    fun toLong(value: Instant): Long {
        return value.toEpochMilli()
    }

    @TypeConverter
    fun fromLong(long: Long): Instant {
        return Instant.ofEpochMilli(long)
    }
}