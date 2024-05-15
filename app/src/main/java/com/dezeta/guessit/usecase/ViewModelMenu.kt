package com.dezeta.guessit.usecase

import androidx.lifecycle.ViewModel
import com.dezeta.guessit.domain.Repository.Repository
import com.dezeta.guessit.domain.entity.Guess
import kotlin.random.Random

class ViewModelMenu : ViewModel() {
    var previousNum: Int? = null
    fun getSerie(): Guess {
        val lista = Repository.getOnlineSeriesList()
        var r:Int
        do {
            r = Random.nextInt(lista.size)
        }while (r == previousNum)
        previousNum = r
        return lista[r]
    }
}