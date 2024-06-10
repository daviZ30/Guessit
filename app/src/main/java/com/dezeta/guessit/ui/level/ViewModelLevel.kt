package com.dezeta.guessit.ui.level

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezeta.guessit.domain.Repository.Repository
import com.dezeta.guessit.domain.Repository.Resource
import com.dezeta.guessit.domain.entity.Guess
import com.dezeta.guessit.domain.entity.ProviderType
import com.dezeta.guessit.domain.entity.User
import com.dezeta.guessit.utils.Locator
import com.dezeta.guessit.domain.Repository.UserManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModelLevel : ViewModel() {
    lateinit var user: User
    private var state = MutableLiveData<LevelState>()
    fun getState(): LiveData<LevelState> {
        return state
    }

    fun loadUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = Locator.userManager.loadUser()
            if (result is Resource.Success<*>) {
                user = result.data as User
                withContext(Dispatchers.Main) {
                    state.value = LevelState.Success(user)
                }
            }
        }
    }

    fun getGuess(id: String): Guess {
        return Repository.getGuessFromId(id)
    }

    fun updateLevel() {
        user.level++
        viewModelScope.launch(Dispatchers.IO) {
            Locator.userManager.UpdateUser(user)
        }
    }
}
