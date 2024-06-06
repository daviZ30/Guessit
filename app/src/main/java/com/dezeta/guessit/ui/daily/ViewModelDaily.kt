package com.dezeta.guessit.ui.daily

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezeta.guessit.domain.Repository.Repository
import com.dezeta.guessit.domain.Repository.Resource
import com.dezeta.guessit.domain.entity.Category
import com.dezeta.guessit.domain.entity.Img
import com.dezeta.guessit.domain.entity.Guess
import com.dezeta.guessit.domain.entity.GuessType
import com.dezeta.guessit.domain.entity.ProviderType
import com.dezeta.guessit.domain.entity.User
import com.dezeta.guessit.utils.Locator
import com.dezeta.guessit.domain.Repository.UserManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class ViewModelDaily : ViewModel() {
    var dataBase = FirebaseFirestore.getInstance()
    var serie: Guess? = null
    var local = false
    var help = true
    var error = 0
    var point = 60
    var user: User? = null
    private var state = MutableLiveData<DailyState>()
    fun getState(): LiveData<DailyState> {
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
            }
        }
    }

    fun getSerieList(): List<Guess> {
        return Repository.getSeriesList()
    }

    fun getPlayerNameList(): List<String> {
        return Repository.getPlayerName()
    }


    fun getSerieFromName(name: String): Guess {
        return Repository.getSerieFromName(name)
    }

    fun categoryList(): List<String> {
        return Category.entries.map { it.name }.dropLast(1)
    }

    fun getFilterList(str: String): MutableList<String> {
        val lista = mutableListOf<String>()
        when (serie?.guessType) {
            GuessType.COUNTRY -> {
                getCountryNameList().forEach {
                    if (it.uppercase().contains(str.uppercase())) {
                        lista.add(it)
                    }
                }
            }

            GuessType.SERIE -> {
                Repository.getSerieName()?.forEach {
                    if (it.uppercase().contains(str.uppercase())) {
                        lista.add(it)
                    }
                }
            }

            GuessType.FOOTBALL -> {
                Repository.getPlayerName().forEach {
                    if (it.uppercase().contains(str.uppercase())) {
                        lista.add(it)
                    }
                }
            }

            else -> {
                Repository.getLocalList().forEach {
                    if (it.name.uppercase().contains(str.uppercase())) {
                        lista.add(it.name)
                    }
                }
            }
        }

        return lista
    }

    fun getImages(serie: Guess): List<Img>? {
        return when (val result = Repository.getImageFromId(serie.id)) {
            is Resource.Error -> null
            is Resource.Success<*> -> result.data as List<Img>
        }
    }

    fun getCountryNameList(): MutableList<String> {
        val list = mutableListOf<String>()
        val isoCountryCodes = Locale.getISOCountries()
        for (countryCode in isoCountryCodes) {
            val locale = Locale(Locale.getDefault().isO3Language, countryCode)
            val countryName = locale.displayCountry
            list.add(countryName)
        }
        return list
    }

    fun updatePoint() {
        UserManager.UpdatePoint(point)
    }

    fun update2500Point() {
        user!!.point += 2500
        user!!.completeLevel = 24
        viewModelScope.launch(Dispatchers.IO) {
            dataBase.collection("users").document(user!!.email).set(
                hashMapOf(
                    "provider" to user!!.provider,
                    "email" to user!!.email,
                    "point" to user!!.point,
                    "level" to user!!.level,
                    "completeLevel" to user!!.completeLevel
                )
            ).addOnSuccessListener {
                state.value = DailyState.insertSuccess
            }
        }
    }

    fun updateLevel(level: Int) {
        val email = Locator.email
        viewModelScope.launch(Dispatchers.IO) {
            dataBase.collection("users").document(email).get().addOnSuccessListener {
                val user = User(
                    it.get("email") as String,
                    it.get("name") as String,
                    it.get("friends") as List<String>,
                    (it.get("point") as Number).toInt(),
                    ProviderType.valueOf(it.get("provider") as String),
                    (it.get("level") as Number).toInt(),
                    "",
                    level,
                    (it.get("countryEnable") as Boolean),
                    (it.get("serieEnable") as Boolean),
                    (it.get("footballEnable") as Boolean),
                )
                if (level >= (it.get("completeLevel") as Number).toInt()) {
                    dataBase.collection("users").document(user.email).set(
                        hashMapOf(
                            "email" to user.email,
                            "name" to user.name,
                            "friends" to user.friends,
                            "point" to user.point,
                            "provider" to user.provider,
                            "level" to user.level,
                            "completeLevel" to user.completeLevel,
                            "countryEnable" to user.countryEnable,
                            "serieEnable" to user.serieEnable,
                            "footballEnable" to user.footballEnable,
                        )
                    )
                }
            }.addOnSuccessListener {
                state.value = DailyState.insertSuccess
            }
        }
    }

}