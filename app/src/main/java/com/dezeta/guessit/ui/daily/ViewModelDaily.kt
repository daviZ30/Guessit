package com.dezeta.guessit.ui.daily

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

    fun getCountryNameList(): MutableList<String> {
        val list = mutableListOf<String>()
        val isoCountryCodes = Locale.getISOCountries()
        for (countryCode in isoCountryCodes) {
            val locale = Locale(Locale.getDefault().isO3Language, countryCode)
            val countryName = locale.displayCountry
            list.add(countryName)
        }
        return list
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
                    withContext(Dispatchers.Main){
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
                withContext(Dispatchers.Main){
                    state.value = DailyState.Success
                }
        }
    }
}