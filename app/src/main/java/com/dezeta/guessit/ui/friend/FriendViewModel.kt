package com.dezeta.guessit.ui.friend

import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezeta.guessit.domain.entity.ProviderType
import com.dezeta.guessit.domain.entity.User
import com.dezeta.guessit.ui.menu.ExtraState
import com.dezeta.guessit.utils.CloudStorageManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class FriendViewModel : ViewModel() {
    private var state = MutableLiveData<FriendState>()
    private var manager = CloudStorageManager()
    fun getState(): LiveData<FriendState> {
        return state
    }

    var dataBase = FirebaseFirestore.getInstance()
    var userList: MutableList<User> = mutableListOf()
    fun getUserProfileImageByEmail(email: String, view: ImageView) {
        viewModelScope.launch {
            manager.getUserImages("")
        }
    }

    fun getAllUserAccounts() {
        val usersRef = dataBase.collection("users")
        usersRef.get().addOnCompleteListener { task ->
            userList.clear()
            if (task.isSuccessful) {
                viewModelScope.launch {
                    for (document in task.result) {
                        val userData = document.data
                        val user = User(
                            userData["email"] as String,
                            (userData["point"] as Number).toInt(),
                            ProviderType.valueOf(userData["provider"] as String),
                            (userData["level"] as Number).toInt(),
                            manager.getUserImages(userData["email"] as String),
                            (userData["completeLevel"] as Number).toInt()
                        )
                        userList.add(
                            user
                        )
                    }
                    state.value = FriendState.AddFriend
                }
            } else {
                println("Error al obtener documentos: ${task.exception}")
            }
        }

    }
}