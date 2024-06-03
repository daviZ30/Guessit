package com.dezeta.guessit.ui.friend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezeta.guessit.domain.entity.ProviderType
import com.dezeta.guessit.domain.entity.User
import com.dezeta.guessit.utils.CloudStorageManager
import com.dezeta.guessit.utils.Locator
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class FriendViewModel : ViewModel() {
    private var state = MutableLiveData<FriendState>()
    private var manager = CloudStorageManager()
    var dataBase = FirebaseFirestore.getInstance()
    var userList: MutableList<User> = mutableListOf()
    fun getState(): LiveData<FriendState> {
        return state
    }

    fun loadUser(email: String) {
        dataBase.collection("users").document(email).get().addOnSuccessListener {
            viewModelScope.launch {
                val user = User(
                    it.get("email") as String,
                    it.get("friends") as List<String>,
                    (it.get("point") as Number).toInt(),
                    ProviderType.valueOf(it.get("provider") as String),
                    (it.get("level") as Number).toInt(),
                    manager.getUserImages(it.get("email") as String),
                    (it.get("completeLevel") as Number).toInt(),
                )
                userList.add(user)
                state.value = FriendState.AddFriend
            }
        }
    }

    fun getAllFriend() {
        val usersRef = dataBase.collection("users")
        usersRef.document(Locator.email).get().addOnSuccessListener {
            val friends: List<String> = (it.get("friends") as List<String>)
            println(friends)
            for (f in friends) {
                loadUser(f)
            }
        }
    }
}