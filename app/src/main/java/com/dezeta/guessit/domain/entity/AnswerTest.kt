package com.dezeta.guessit.domain.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "answerTest",
    primaryKeys = ["guessId", "answer"],
    foreignKeys = [ForeignKey(
        entity = Guess::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("guessId"),
        onDelete = ForeignKey.RESTRICT,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class AnswerTest(
    val guessId: String, val answer: String, val correct: Boolean
)
