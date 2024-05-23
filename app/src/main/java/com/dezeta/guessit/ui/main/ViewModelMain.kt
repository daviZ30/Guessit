package com.dezeta.guessit.ui.main

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezeta.guessit.domain.Repository.Repository
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
                it.get("email") as String, /* it.get("point") as Int*/0,
                ProviderType.valueOf(it.get("provider") as String),
            )
            state.value = MainState.UserSuccess(
                user.value!!
            )
        }

    }

    fun saveUser(u: User) {
        dataBase.collection("users").document(u.email).set(
            hashMapOf(
                "provider" to u.provider,
                "email" to u.email,
                "point" to u.point
            )
        ).addOnCompleteListener {
            user.value = u
            state.value = MainState.RefreshUser(u)
        }
    }

    fun saveImageProfile(manager: CloudStorageManager, uri: Uri) {
        viewModelScope.launch {
            manager.updateFile(user.value!!.email,uri)
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