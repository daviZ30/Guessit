package com.dezeta.guessit.ui.level

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dezeta.guessit.domain.Repository.Repository
import com.dezeta.guessit.domain.entity.Guess
import com.dezeta.guessit.domain.entity.ProviderType
import com.dezeta.guessit.domain.entity.User
import com.dezeta.guessit.utils.Locator
import com.dezeta.guessit.utils.UserManager
import com.google.firebase.firestore.FirebaseFirestore

class ViewModelLevel : ViewModel() {
    lateinit var user: User
    var dataBase = FirebaseFirestore.getInstance()
    private var state = MutableLiveData<LevelState>()
    fun getState(): LiveData<LevelState> {
        return state
    }

    fun loadUser() {
        val email = Locator.email
        dataBase.collection("users").document(email).get().addOnSuccessListener {
            user = User(
                it.get("email") as String,
                (it.get("point") as Number).toInt(),
                ProviderType.valueOf(it.get("provider") as String),
                (it.get("level") as Number).toInt(),
                (it.get("completeLevel") as Number).toInt(),
            )
            state.value = LevelState.Success(user)
        }
    }

    fun getGuess(id: String): Guess {
        return Repository.getGuessFromId(id)
    }

    fun updateLevel() {
        user.level++
        UserManager.UpdateUser(user)
    }
}
