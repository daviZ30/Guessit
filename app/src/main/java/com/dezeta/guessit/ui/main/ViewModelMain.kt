package com.dezeta.guessit.ui.main

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezeta.guessit.domain.Repository.Repository
import com.dezeta.guessit.domain.Repository.Resource
import com.dezeta.guessit.domain.entity.Guess
import com.dezeta.guessit.domain.entity.ProviderType
import com.dezeta.guessit.domain.entity.User
import com.dezeta.guessit.utils.CloudStorageManager
import com.dezeta.guessit.utils.Locator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModelMain : ViewModel() {
    private var state = MutableLiveData<MainState>()
    var user = MutableLiveData<User>()


    fun getState(): LiveData<MainState> {
        return state
    }

    fun loadUser(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = Locator.userManager.loadUser(email)
            if (result is Resource.Success<*>) {
                withContext(Dispatchers.Main) {
                    state.value = MainState.UserSuccess(
                        result.data as User
                    )
                }
            }
        }
    }

    fun deleteUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val res = Locator.userManager.deleteUser()
            if (res is Resource.Success<*>) {
                withContext(Dispatchers.Main) {
                    state.value = MainState.SignOut
                }
            }
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