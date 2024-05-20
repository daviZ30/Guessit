package com.dezeta.guessit.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dezeta.guessit.domain.Repository.Repository
import com.dezeta.guessit.domain.Repository.Resource
import com.dezeta.guessit.domain.entity.Guess

class ViewModelLocalGame : ViewModel() {
    private var state = MutableLiveData<LocalGameState>()
    var createdGames = MutableLiveData(Repository.getLocalList().size.toString())
    fun updateStatistics(){
        createdGames.value = Repository.getLocalList().size.toString()
    }

    fun getLevelList(): List<Guess> {
        return Repository.getLocalList()
    }

    fun deletefromId(id: String) {
        when (val result = Repository.deleteFromId(id)) {
            is Resource.Error -> state.value = LocalGameState.DeleteError(result.exception)
            is Resource.Success<*> ->  state.value = LocalGameState.Success
        }
    }

    fun getState(): LiveData<LocalGameState> {
        return state
    }

}