package com.dezeta.guessit.domain.Repository

import android.database.sqlite.SQLiteException
import com.dezeta.guessit.domain.SerieDataBase
import com.dezeta.guessit.domain.entity.Img
import com.dezeta.guessit.domain.entity.Info
import com.dezeta.guessit.domain.entity.Guess

class Repository {
    companion object {
        fun isLocalNewId(id: String): Boolean {
            val resultado = SerieDataBase.getInstance().GuessDao().selectAllLocal()
                .find { it.id.contains(id, ignoreCase = true) }
            return resultado == null

        }

        fun isLocalNewName(name: String): Boolean {
            val resultado = SerieDataBase.getInstance().GuessDao().selectAllLocal()
                .find { it.name.contains(name, ignoreCase = true) }
            return resultado == null
        }

        fun insertGuess(serie: Guess): Resource {
            return try {
                SerieDataBase.getInstance().GuessDao().insert(serie)
                Resource.Success<Guess>(serie)
            } catch (e: SQLiteException) {
                println(e.message)
                Resource.Error(e)
            }
        }

        fun insertImages(images: List<Img>): Resource {
            return try {
                images.forEach {
                    SerieDataBase.getInstance().imgDao().insert(it)
                }
                Resource.Success(images)
            } catch (e: SQLiteException) {
                println(e.message)
                Resource.Error(e)
            }
        }

        fun getLocalList(): List<Guess> {
            return SerieDataBase.getInstance().GuessDao().selectAllLocal()
        }

        fun getSeriesList(): List<Guess> {
            return SerieDataBase.getInstance().GuessDao().selectAllSerie()
        }

        fun getCountryList(): List<Guess> {
            return SerieDataBase.getInstance().GuessDao().selectAllCountry()
        }
        fun getSerieFromName(name: String): Guess {
            return SerieDataBase.getInstance().GuessDao().selectSerieFromName(name)
        }

        fun deleteFromId(id: String): Resource {
            return try {
                SerieDataBase.getInstance().infoDao().deleteFromId(id)
                SerieDataBase.getInstance().imgDao().deleteFromId(id)
                SerieDataBase.getInstance().GuessDao().deleteFromId(id)
                Resource.Success(null)
            } catch (e: SQLiteException) {
                println(e.message)
                Resource.Error(e)
            }
        }

        fun getImageFromId(id: String): Resource {
            return try {
                Resource.Success(SerieDataBase.getInstance().imgDao().selectFromId(id))
            } catch (e: SQLiteException) {
                println(e.message)
                Resource.Error(e)
            }
        }

        fun getSerieName(): List<String>? {
            return try {
                SerieDataBase.getInstance().GuessDao().selectSerieName()
            } catch (e: SQLiteException) {
                println(e.message)
                null
            }
        }

        fun getImage0(id: String): Img? {
            var lista = SerieDataBase.getInstance().imgDao().selectFromId(id)
            var img: Img? = null
            lista.forEach { if (it.order == 0) img = it }
            return img
        }
        fun getInfoFromId(id: String): Info {
            return SerieDataBase.getInstance().infoDao().select(id)
        }



    }
}