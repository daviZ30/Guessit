package com.dezeta.guessit.ui.level

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dezeta.guessit.domain.Repository.Repository
import com.dezeta.guessit.domain.Repository.Resource
import com.dezeta.guessit.domain.entity.Guess
import com.dezeta.guessit.domain.entity.ProviderType
import com.dezeta.guessit.domain.entity.User
import com.dezeta.guessit.ui.login.LoginState
import com.dezeta.guessit.ui.main.MainState
import com.dezeta.guessit.utils.Locator
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
            println("OBTENER COSAS")
            user = User(
                it.get("email") as String,
                (it.get("point") as Number).toInt(),
                ProviderType.valueOf(it.get("provider") as String),
                (it.get("level") as Number).toInt(),
            )
            state.value = LevelState.Success(user)
        }
    }

    fun getGuess(id: String): Guess {
        return Repository.getGuessFromId(id)
    }

    fun updateLevel() {
        user.level++
        dataBase.collection("users").document(user.email).set(
            hashMapOf(
                "provider" to user.provider,
                "email" to user.email,
                "point" to user.point,
                "level" to user.level
            )
        )
    }
}
