package com.dezeta.guessit.ui.level

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezeta.guessit.domain.Repository.Repository
import com.dezeta.guessit.domain.entity.Guess
import com.dezeta.guessit.domain.entity.ProviderType
import com.dezeta.guessit.domain.entity.User
import com.dezeta.guessit.utils.Locator
import com.dezeta.guessit.domain.Repository.UserManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewModelLevel : ViewModel() {
    lateinit var user: User
    var dataBase = FirebaseFirestore.getInstance()
    private var state = MutableLiveData<LevelState>()
    fun getState(): LiveData<LevelState> {
        return state
    }

    fun loadUser() {
        val email = Locator.email
        viewModelScope.launch(Dispatchers.IO) {
            dataBase.collection("users").document(email).get().addOnSuccessListener {
                user = User(
                    it.get("email") as String,
                    it.get("name") as String,
                    it.get("friends") as List<String>,
                    (it.get("point") as Number).toInt(),
                    ProviderType.valueOf(it.get("provider") as String),
                    (it.get("level") as Number).toInt(),
                    "",
                    (it.get("completeLevel") as Number).toInt(),
                    (it.get("countryEnable") as Boolean),
                    (it.get("serieEnable") as Boolean),
                    (it.get("footballEnable") as Boolean),
                )
                state.value = LevelState.Success(user)
            }
        }
    }

    fun getGuess(id: String): Guess {
        return Repository.getGuessFromId(id)
    }

    fun updateLevel() {
        user.level++
        viewModelScope.launch(Dispatchers.IO) {
            UserManager.UpdateUser(user)
        }
    }
}
