package com.dezeta.guessit.domain.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.Query
import com.dezeta.guessit.domain.entity.AnswerTest
import com.dezeta.guessit.domain.entity.Img

@Dao
interface AnswerTestDao {
    @Insert(onConflict = ForeignKey.RESTRICT)
    fun insert (answerTest: AnswerTest) : Long

    @Delete
    fun delete(answerTest: AnswerTest)

    @Query("DELETE FROM answerTest WHERE guessId = :guessId")
    fun deleteFromId(guessId: String)

    @Query("SELECT * FROM answerTest WHERE guessId = :guessId")
    fun selectFromId(guessId: String) : List<AnswerTest>
}