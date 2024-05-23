package com.dezeta.guessit.ui.menu

import android.provider.ContactsContract.CommonDataKinds.Email
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezeta.guessit.domain.Repository.Repository
import com.dezeta.guessit.domain.entity.Guess
import com.dezeta.guessit.domain.entity.ProviderType
import com.dezeta.guessit.domain.entity.User
import com.dezeta.guessit.ui.main.MainState
import com.dezeta.guessit.utils.CloudStorageManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlin.random.Random

class ViewModelMenu : ViewModel() {
    var guessSerie = MutableLiveData<String>("0")
    var guessGame = MutableLiveData<String>("0")
    var guessPlayer = MutableLiveData<String>("0")
    var guessCountry = MutableLiveData<String>("0")
    var dataBase = FirebaseFirestore.getInstance()
    var userList = mutableListOf<User>()
    private var state = MutableLiveData<ExtraState>()

    var previousNum: Int? = null
    fun getSerie(): Guess {
        val lista = Repository.getSeriesList()
        var r:Int
        do {
            r = Random.nextInt(lista.size)
        }while (r == previousNum)
        previousNum = r
        return lista[r]
    }

    fun getCountry(): Guess {
        val lista = Repository.getCountryList()
        var r:Int
        do {
            r = Random.nextInt(lista.size)
        }while (r == previousNum)
        previousNum = r
        return lista[r]
    }
    fun getState(): LiveData<ExtraState> {
        return state
    }
    fun getUserProfileImageByEmail(manager: CloudStorageManager,email: String,view: ImageView) {
        viewModelScope.launch {
            state.value = ExtraState.refreshUserProfile(view,manager.getUserImages(email))
        }

    }
    fun getAllUserAccounts() {
        val usersRef = dataBase.collection("users")
        usersRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    val documentId = document.id
                    val userData = document.data
                    // Aquí puedes procesar los datos de cada documento
                    // Por ejemplo, imprimir el ID y los datos del documento
                    println("ID del documento: $documentId")
                    println("Datos del documento: $userData")

                    userList.add(
                        User(
                            userData.get("email") as String,
                            (userData.get("point") as Number).toInt(),
                            ProviderType.valueOf(userData.get("provider") as String),
                        )
                    )
                    state.value = ExtraState.refreshUserList

                }
            } else {
                println("Error al obtener documentos: ${task.exception}")
            }
        }
    }
}