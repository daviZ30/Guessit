package com.dezeta.guessit.usecase

import androidx.lifecycle.ViewModel
import com.dezeta.guessit.domain.Repository.Repository
import com.dezeta.guessit.domain.Repository.Resource
import com.dezeta.guessit.domain.entity.Category
import com.dezeta.guessit.domain.entity.Img
import com.dezeta.guessit.domain.entity.Guess

class ViewModelDaily : ViewModel() {
    var local = false
    var help = true
    var error = 0

    fun getSerieList(): List<Guess> {
        return Repository.getSeriesList()
    }

    fun getSerieFromName(name:String):Guess{
        return Repository.getSerieFromName(name)
    }

    fun categoryList(): List<String> {
        return Category.entries.map {it.name}.dropLast(1)
    }

    fun getFilterList(str: String): MutableList<String> {
        val lista = mutableListOf<String>()

        if (local) {
            Repository.getLocalList().forEach {
                if (it.name.uppercase().contains(str.uppercase())) {
                    lista.add(it.name)
                }
            }
        } else {
            Repository.getSerieName()?.forEach {
                if (it.uppercase().contains(str.uppercase())) {
                    lista.add(it)
                }
            }
        }
        return lista
    }

    fun getImages(serie: Guess): List<Img>? {
        return when (val result = Repository.getImageFromId(serie.id)) {
            is Resource.Error -> null
            is Resource.Success<*> -> result.data as List<Img>
        }
    }
}