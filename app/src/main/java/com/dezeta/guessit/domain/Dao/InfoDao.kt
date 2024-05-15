package com.dezeta.guessit.domain.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.Query
import com.dezeta.guessit.domain.entity.Info

@Dao
interface InfoDao {
    @Insert(onConflict = ForeignKey.RESTRICT)
    fun insert(info: Info): Long

    @Query("SELECT * FROM info")
    fun selectAll(): List<Info>

    @Query("SELECT * FROM info WHERE serieId = :serieId")
    fun select(serieId:String): Info

    @Delete
    fun delete(info: Info)
    @Query("DELETE FROM info WHERE serieId = :serieId")
    fun deleteFromId(serieId: String)
}