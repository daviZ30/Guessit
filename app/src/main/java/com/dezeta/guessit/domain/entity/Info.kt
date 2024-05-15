package com.dezeta.guessit.domain.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dezeta.guessit.domain.converter.InstantConverter
import java.time.Instant

@Entity(
    tableName = "info", foreignKeys = [ForeignKey(
        entity = Guess::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("serieId"),
        onDelete = ForeignKey.RESTRICT,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Info(
    @PrimaryKey
    val serieId: String,
    val IMDB: Double,
    @TypeConverters(InstantConverter::class)
    val release: Instant,
    val synapse: String
)