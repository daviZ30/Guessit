package com.dezeta.guessit.domain.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.Query
import com.dezeta.guessit.domain.entity.Img

@Dao
interface ImgDao {
    @Insert(onConflict = ForeignKey.RESTRICT)
    fun insert (img: Img) : Long

    @Query("SELECT * FROM img")
    fun selectAll(): List<Img>

    @Delete
    fun delete(img: Img)

    @Query("DELETE FROM img WHERE serieId = :serieId")
    fun deleteFromId(serieId: String)

    @Query("SELECT * FROM img WHERE serieId = :serieId")
    fun selectFromId(serieId: String) : List<Img>



}