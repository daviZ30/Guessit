package com.dezeta.guessit.ui.daily

import androidx.lifecycle.ViewModel
import com.dezeta.guessit.domain.Repository.Repository
import com.dezeta.guessit.domain.Repository.Resource
import com.dezeta.guessit.domain.entity.Category
import com.dezeta.guessit.domain.entity.Img
import com.dezeta.guessit.domain.entity.Guess
import com.dezeta.guessit.domain.entity.GuessType
import java.util.Locale

class ViewModelDaily : ViewModel() {
    var serie: Guess? = null
    var local = false
    var help = true
    var error = 0

    fun getSerieList(): List<Guess> {
        return Repository.getSeriesList()
    }

    fun getSerieFromName(name: String): Guess {
        return Repository.getSerieFromName(name)
    }

    fun categoryList(): List<String> {
        return Category.entries.map { it.name }.dropLast(1)
    }

    fun getFilterList(str: String): MutableList<String> {
        val lista = mutableListOf<String>()
        if (serie?.guessType == GuessType.COUNTRY) {
            getCountryNameList().forEach {
                if (it.uppercase().contains(str.uppercase())) {
                    lista.add(it)
                }
            }
        } else {
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
        }

        return lista
    }

    fun getImages(serie: Guess): List<Img>? {
        return when (val result = Repository.getImageFromId(serie.id)) {
            is Resource.Error -> null
            is Resource.Success<*> -> result.data as List<Img>
        }
    }

    fun getCountryNameList(): MutableList<String> {
        var list = mutableListOf<String>()
        val isoCountryCodes = Locale.getISOCountries()
        for (countryCode in isoCountryCodes) {
            val locale = Locale(Locale.getDefault().isO3Language, countryCode)
            val countryName = locale.displayCountry
            list.add(countryName)
        }
        return list
    }
}