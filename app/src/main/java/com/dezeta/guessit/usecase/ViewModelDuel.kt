package com.dezeta.guessit.usecase

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dezeta.guessit.domain.Repository.Repository
import com.dezeta.guessit.domain.entity.Img
import com.dezeta.guessit.domain.entity.Info
import com.dezeta.guessit.domain.entity.Guess
import kotlin.random.Random

class ViewModelDuel : ViewModel() {
    var score = MutableLiveData<String>()
    var previousNum: Int? = null
    fun getSerie(): Guess {
        val lista = Repository.getSeriesList()
        var r: Int
        do {
            r = Random.nextInt(lista.size)
        } while (r == previousNum)
        previousNum = r
        return lista[r]
    }

    fun getImage0(serie: Guess): Img? {
        return Repository.getImage0(serie.id)
    }

    fun getInfoFromId(serie: Guess): Info {
        return Repository.getInfoFromId(serie.id)
    }

}