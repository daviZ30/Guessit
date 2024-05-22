package com.dezeta.guessit.ui.menu

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dezeta.guessit.domain.Repository.Repository
import com.dezeta.guessit.domain.entity.Guess
import kotlin.random.Random

class ViewModelMenu : ViewModel() {
    var guessSerie = MutableLiveData<String>("0")
    var guessGame = MutableLiveData<String>("0")
    var guessPlayer = MutableLiveData<String>("0")
    var guessCountry = MutableLiveData<String>("0")

    var previousNum: Int? = null
    fun getSerie(): Guess {
        val lista = Repository.getSeriesList()
        var r:Int
        do {
            r = Random.nextInt(lista.size)
        }while (r == previousNum)
        previousNum = r
        return lista[r]
    }

    fun getCountry(): Guess {
        val lista = Repository.getCountryList()
        var r:Int
        do {
            r = Random.nextInt(lista.size)
        }while (r == previousNum)
        previousNum = r
        return lista[r]
    }
}