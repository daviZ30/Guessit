package com.dezeta.guessit.ui.menu

import android.content.SharedPreferences
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezeta.guessit.domain.Repository.Repository
import com.dezeta.guessit.domain.Repository.UserManager
import com.dezeta.guessit.domain.entity.Guess
import com.dezeta.guessit.domain.entity.ProviderType
import com.dezeta.guessit.domain.entity.User
import com.dezeta.guessit.ui.main.MainState
import com.dezeta.guessit.utils.CloudStorageManager
import com.dezeta.guessit.utils.Locator
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class ViewModelMenu : ViewModel() {
    var guessSerie = MutableLiveData<String>("0")
    var guessGame = MutableLiveData<String>("0")
    var guessPlayer = MutableLiveData<String>("0")
    var guessCountry = MutableLiveData<String>("0")
    var dataBase = FirebaseFirestore.getInstance()
    var userList = mutableListOf<User>()
    var user: User? = null
    private var state = MutableLiveData<ExtraState>()

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

    fun getTestList(): List<Guess> {
        return Repository.getTestList()
    }

    fun getCountry(): Guess {
        val lista = Repository.getCountryList()
        var r: Int
        do {
            r = Random.nextInt(lista.size)
        } while (r == previousNum)
        previousNum = r
        return lista[r]
    }

    fun getPlayer(): Guess {
        val lista = Repository.getPlayerList()
        var r: Int
        do {
            r = Random.nextInt(lista.size)
        } while (r == previousNum)
        previousNum = r
        return lista[r]
    }

    fun getState(): LiveData<ExtraState> {
        return state
    }

    fun getUserProfileImageByEmail(manager: CloudStorageManager, email: String, view: ImageView) {
        viewModelScope.launch {
            state.value = ExtraState.refreshUserProfile(view, manager.getUserImages(email))
        }
    }

    fun getAllUserAccounts() {
        val usersRef = dataBase.collection("users")
        viewModelScope.launch(Dispatchers.Default) {
            usersRef.get().addOnCompleteListener { task ->
                userList.clear()
                if (task.isSuccessful) {
                    for (document in task.result) {
                        val userData = document.data
                        val user = User(
                            userData["email"] as String,
                            userData["name"] as String,
                            userData["friends"] as List<String>,
                            (userData["point"] as Number).toInt(),
                            ProviderType.valueOf(userData["provider"] as String),
                            (userData["level"] as Number).toInt(),
                            "",
                            (userData["completeLevel"] as Number).toInt(),
                            userData["countryEnable"] as Boolean,
                            userData["serieEnable"] as Boolean,
                            userData["footballEnable"] as Boolean,
                        )
                        userList.add(
                            user
                        )
                    }
                    state.value = ExtraState.refreshUserList
                } else {
                    println("Error al obtener documentos: ${task.exception}")
                }
            }
        }
    }

    fun loadUser() {
        viewModelScope.launch(Dispatchers.IO) {
            dataBase.collection("users").document(Locator.email).get().addOnSuccessListener {
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
                state.value = ExtraState.UserSuccess
            }
        }
    }

    fun saveUser() {
        UserManager.saveUser(user!!)
    }
}