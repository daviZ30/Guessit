package com.dezeta.guessit.ui.menu

import android.content.SharedPreferences
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezeta.guessit.domain.Repository.Repository
import com.dezeta.guessit.domain.Repository.Resource
import com.dezeta.guessit.domain.Repository.UserManager
import com.dezeta.guessit.domain.entity.Guess
import com.dezeta.guessit.domain.entity.ProviderType
import com.dezeta.guessit.domain.entity.User
import com.dezeta.guessit.ui.friend.FriendState
import com.dezeta.guessit.ui.main.MainState
import com.dezeta.guessit.utils.CloudStorageManager
import com.dezeta.guessit.utils.Locator
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class ViewModelMenu : ViewModel() {
    var guessSerie = MutableLiveData<String>("0")
    var guessGame = MutableLiveData<String>("0")
    var guessPlayer = MutableLiveData<String>("0")
    var guessCountry = MutableLiveData<String>("0")
    private var manager = CloudStorageManager()
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
        viewModelScope.launch(Dispatchers.Default) {
            val result = Locator.userManager.getDocuments()
            if (result is Resource.Success<*>) {
                val task = result.data as QuerySnapshot
                userList.clear()
                for (document in task) {
                    val userData = document.data
                    val user = User(
                        userData[UserManager.EMAIL] as String,
                        userData[UserManager.NAME] as String,
                        userData[UserManager.FRIENDS] as List<String>,
                        (userData[UserManager.POINT] as Number).toInt(),
                        ProviderType.valueOf(userData[UserManager.PROVIDER] as String),
                        (userData[UserManager.LEVEL] as Number).toInt(),
                        manager.getUserImages(userData[UserManager.EMAIL] as String),
                        (userData[UserManager.COMPLETE_LEVEL] as Number).toInt(),
                        (userData[UserManager.COUNTRY_ENABLE] as Boolean),
                        (userData[UserManager.SERIE_ENABLE] as Boolean),
                        (userData[UserManager.FOOTBALL_ENABLE] as Boolean),
                        (userData[UserManager.STAT_COUNTRY] as Number).toInt(),
                        (userData[UserManager.STAT_SERIE] as Number).toInt(),
                        (userData[UserManager.STAT_FOOTBALL] as Number).toInt(),
                    )
                    userList.add(
                        user
                    )
                }
                state.value = ExtraState.refreshUserList

            }
        }
    }

    fun loadUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = Locator.userManager.loadUser()
            if (result is Resource.Success<*>) {
                user = result.data as User
                state.value = ExtraState.UserSuccess
            }
        }
    }

    fun loadUserAndSave(s: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loadUser()
            when (s) {
                "country" -> {
                    user!!.countryEnable = false
                }

                "serie" -> {
                    user!!.serieEnable = false
                }

                "football" -> {
                    user!!.footballEnable = false
                }
            }
            Locator.userManager.saveUser(user!!)
        }
    }
}