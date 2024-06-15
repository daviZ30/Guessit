package com.dezeta.guessit.ui.daily

import androidx.compose.ui.text.toUpperCase
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezeta.guessit.domain.Repository.Repository
import com.dezeta.guessit.domain.Repository.Resource
import com.dezeta.guessit.domain.entity.Category
import com.dezeta.guessit.domain.entity.Img
import com.dezeta.guessit.domain.entity.Guess
import com.dezeta.guessit.domain.entity.GuessType
import com.dezeta.guessit.domain.entity.User
import com.dezeta.guessit.utils.Locator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class ViewModelDaily : ViewModel() {
    var guess: Guess? = null
    var local = false
    var help = true
    var error = 0
    var point = 60
    var user: User? = null
    private var state = MutableLiveData<DailyState>()
    var translateToEnglish =
        getCountrySpain().map { it.uppercase(Locale.ROOT) }.zip(getCountryEnglish()).toMap()
    var translateToSpanish =
        getCountryEnglish().map { it.uppercase(Locale.ROOT) }.zip(getCountrySpain()).toMap()

    fun getState(): LiveData<DailyState> {
        return state
    }

    fun loadUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = Locator.userManager.loadUser()
            if (result is Resource.Success<*>)
                user = result.data as User
        }
    }

    fun getSerieList(): List<Guess> {
        return Repository.getSeriesList()
    }

    fun getPlayerNameList(): List<String> {
        return Repository.getPlayerName()
    }


    fun getSerieFromName(name: String): Guess {
        return Repository.getSerieFromName(name)
    }

    fun categoryList(): List<String> {
        return Category.entries.map { it.name }.dropLast(1)
    }

    fun getFilterList(str: String): MutableList<String> {
        val lista = mutableListOf<String>()
        when (guess?.guessType) {
            GuessType.COUNTRY -> {
                getCountryNameList().forEach {
                    if (it.uppercase().contains(str.uppercase())) {
                        lista.add(it)
                    }
                }
            }

            GuessType.SERIE -> {
                Repository.getSerieName()?.forEach {
                    if (it.uppercase().contains(str.uppercase())) {
                        lista.add(it)
                    }
                }
            }

            GuessType.FOOTBALL -> {
                Repository.getPlayerName().forEach {
                    if (it.uppercase().contains(str.uppercase())) {
                        lista.add(it)
                    }
                }
            }

            else -> {
                Repository.getLocalList().forEach {
                    if (it.name.uppercase().contains(str.uppercase())) {
                        lista.add(it.name)
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

    private fun getCountrySpain(): MutableList<String> {
        val list = mutableListOf<String>()

        val isoCountryCodes = Locale.getISOCountries()
        // Set default locale to Spanish
        val defaultLocale = Locale.getDefault()
        Locale.setDefault(Locale("es", "ES"))

        for (countryCode in isoCountryCodes) {
            val locale = Locale("", countryCode)
            val countryName = locale.displayCountry
            println(countryName)
            list.add(countryName)
        }

        Locale.setDefault(defaultLocale)

        return list
    }

    private fun getCountryEnglish(): MutableList<String> {
        val list = mutableListOf<String>()

        val isoCountryCodes = Locale.getISOCountries()
        val defaultLocale = Locale.getDefault()
        Locale.setDefault(Locale.ENGLISH)

        for (countryCode in isoCountryCodes) {
            val locale = Locale("", countryCode)
            val countryName = locale.displayCountry
            println(countryName)
            list.add(countryName)
        }

        Locale.setDefault(defaultLocale)

        return list
    }

    fun getCountryNameList(): MutableList<String> {
        val language = Locator.PreferencesRepository.getLanguage()
        return if (language == "es") {

            println("ESPAÑAAAAAAAAAAAAAAAAAAAAAaa")
            getCountrySpain()
        } else {
            println("INGLESSSSSSSSSSSSSSSSSSSSSSSSSSS")
            getCountryEnglish()
        }

    }

    fun getLanguage(): String {
        return Locator.PreferencesRepository.getLanguage()
    }

    fun updatePoint() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = Locator.userManager.loadUser()
            if (result is Resource.Success<*>) {
                user = result.data as User
                user!!.point += point
                when (guess!!.guessType) {
                    GuessType.FOOTBALL -> {
                        user!!.statFootball += 1
                    }

                    GuessType.COUNTRY -> {
                        user!!.statCountry += 1
                    }

                    GuessType.SERIE -> {
                        user!!.statSerie += 1
                    }

                    else -> {}
                }
                val r = Locator.userManager.UpdateUser(user!!)
                if (r is Resource.Success<*>)
                    withContext(Dispatchers.Main) {
                        state.value = DailyState.Success
                    }
            }

        }

    }

    fun update2500Point() {
        loadUser()
        user!!.point += 2500
        user!!.completeLevel = 24
        viewModelScope.launch(Dispatchers.IO) {
            val result = Locator.userManager.UpdateUser(user!!)
            if (result is Resource.Success<*>) {
                withContext(Dispatchers.Main) {
                    state.value = DailyState.Success
                }
            }
        }
    }

    fun updateLevel(level: Int) {
        loadUser()
        user!!.completeLevel = level
        viewModelScope.launch(Dispatchers.IO) {
            val result = Locator.userManager.UpdateUser(user!!)
            if (result is Resource.Success<*>)
                withContext(Dispatchers.Main) {
                    state.value = DailyState.Success
                }
        }
    }
}