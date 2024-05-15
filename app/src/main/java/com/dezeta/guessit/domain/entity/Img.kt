package com.dezeta.guessit.domain.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "img",
    primaryKeys = ["serieId", "img_url"],
    foreignKeys = [ForeignKey(
        entity = Guess::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("serieId"),
        onDelete = ForeignKey.RESTRICT,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Img(
    val serieId: String,
    val img_url: String,
    val order: Int
)