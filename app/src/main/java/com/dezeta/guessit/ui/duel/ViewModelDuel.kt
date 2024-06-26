package com.dezeta.guessit.ui.duel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezeta.guessit.domain.Repository.Repository
import com.dezeta.guessit.domain.entity.Img
import com.dezeta.guessit.domain.entity.Info
import com.dezeta.guessit.domain.entity.Guess
import com.dezeta.guessit.domain.Repository.UserManager
import com.dezeta.guessit.utils.Locator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class ViewModelDuel : ViewModel() {
    var score = MutableLiveData<String>()
    var previousNum: Int? = null
    var point = 0
    fun updateLevel(level: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            Locator.userManager.UpdateCompleteLevel(level)
        }
    }

    fun getSerie(): Guess {
        val lista = Repository.getSeriesList()
        var r: Int
        do {
            r = Random.nextInt(lista.size)
        } while (r == previousNum)
        previousNum = r
        return lista[r]
    }

    fun updatePoint() {
        viewModelScope.launch(Dispatchers.IO) {
            Locator.userManager.UpdatePoint(point)
        }

    }

    fun getImage0(serie: Guess): Img? {
        return Repository.getImage0(serie.id)
    }

    fun getInfoFromId(serie: Guess): Info {
        return Repository.getInfoFromId(serie.id)
    }

}