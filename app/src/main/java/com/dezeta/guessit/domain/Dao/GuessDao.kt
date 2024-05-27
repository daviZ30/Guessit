package com.dezeta.guessit.domain.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.Query
import com.dezeta.guessit.domain.entity.Guess

@Dao
interface GuessDao {
    @Insert(onConflict = ForeignKey.RESTRICT)
    fun insert(serie: Guess): Long

    @Query("DELETE FROM guess WHERE id = :id")
    fun deleteFromId(id: String)

    @Query("SELECT * FROM guess WHERE guessType == 'LOCAL'")
    fun selectAllLocal(): List<Guess>

    @Query("SELECT * FROM guess WHERE guessType == 'SERIE'")
    fun selectAllSerie(): List<Guess>
    @Query("SELECT * FROM guess WHERE guessType == 'TEST'")
    fun selectAllTest(): List<Guess>
    @Query("SELECT * FROM guess WHERE guessType == 'COUNTRY'")
    fun selectAllCountry(): List<Guess>

    @Query("SELECT * FROM guess WHERE guessType == 'FOOTBALL'")
    fun selectAllPlayer(): List<Guess>

    @Query("SELECT g.name FROM guess g WHERE guessType == 'SERIE'")
    fun selectSerieName(): List<String>


    @Query("SELECT * FROM guess WHERE id == :id")
    fun selectSerieFromId(id: String): Guess

    @Query("SELECT * FROM guess WHERE name == :name")
    fun selectSerieFromName(name: String): Guess

    @Delete
    fun delete(serie: Guess)
}