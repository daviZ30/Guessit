package com.dezeta.guessit.ui.main

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezeta.guessit.domain.Repository.Repository
import com.dezeta.guessit.domain.entity.Guess
import com.dezeta.guessit.domain.entity.ProviderType
import com.dezeta.guessit.domain.entity.User
import com.dezeta.guessit.utils.CloudStorageManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class ViewModelMain : ViewModel() {
    var dataBase = FirebaseFirestore.getInstance()
    private var state = MutableLiveData<MainState>()
    var user = MutableLiveData<User>()


    fun getState(): LiveData<MainState> {
        return state
    }

    fun loadUser(email: String) {
        dataBase.collection("users").document(email).get().addOnSuccessListener {
            user.value = User(
                it.get("email") as String,
                it.get("friends") as List<String>,
                (it.get("point") as Number).toInt(),
                ProviderType.valueOf(it.get("provider") as String),
                (it.get("level") as Number).toInt(),
                "",
                (it.get("completeLevel") as Number).toInt(),
            )
            state.value = MainState.UserSuccess(
                user.value!!
            )
        }
    }

    fun saveImageProfile(manager: CloudStorageManager, uri: Uri) {
        viewModelScope.launch {
            manager.updateFile(user.value!!.email, uri)
            getUserProfileImageByEmail(manager)
        }

    }

    fun getUserProfileImageByEmail(manager: CloudStorageManager) {
        viewModelScope.launch {
            state.value = MainState.RefreshUrl(manager.getUserImages(user.value!!.email))
        }

    }

    fun loadDatabase() {
        Repository.getLocalList()
    }

}