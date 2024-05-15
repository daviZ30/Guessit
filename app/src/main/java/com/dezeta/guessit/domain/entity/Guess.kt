package com.dezeta.guessit.domain.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dezeta.guessit.domain.converter.CategoryConverter
import com.dezeta.guessit.domain.converter.DifficultyConverter
import java.io.Serializable

@Entity(
    tableName = "guess", [Index(value = ["name"], unique = true)]
)
data class Guess(
    @PrimaryKey
    val id: String,
    val name: String,
    @TypeConverters(DifficultyConverter::class)
    val difficulty: Difficulty,
    @TypeConverters(CategoryConverter::class)
    var category: Category
) : Serializable {
    override fun toString(): String {
        return id
    }
}