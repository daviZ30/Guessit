package com.dezeta.guessit.ui.addFriend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezeta.guessit.domain.entity.ProviderType
import com.dezeta.guessit.domain.entity.User
import com.dezeta.guessit.ui.friend.FriendState
import com.dezeta.guessit.utils.CloudStorageManager
import com.dezeta.guessit.utils.Locator
import com.dezeta.guessit.utils.UserManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class AddFriendViewModel : ViewModel() {
    var dataBase = FirebaseFirestore.getInstance()
    var userList = mutableListOf<User>()
    private var state = MutableLiveData<FriendState>()
    private lateinit var friends: List<String>
    private var manager = CloudStorageManager()
    fun load() {
        val usersRef = dataBase.collection("users")
        usersRef.document(Locator.email).get().addOnSuccessListener {
            friends = (it.get("friends") as List<String>)
            /* println(friends)
             for (f in friends) {
             }*/
            getAllUserAccounts()
        }

    }

    fun getState(): LiveData<FriendState> {
        return state
    }

    private fun getAllUserAccounts() {
        val usersRef = dataBase.collection("users")
        usersRef.get().addOnCompleteListener { task ->
            userList.clear()
            if (task.isSuccessful) {
                viewModelScope.launch {
                    for (document in task.result) {
                        val userData = document.data
                        val user = User(
                            userData["email"] as String,
                            userData["name"] as String,
                            userData["friends"] as List<String>,
                            (userData["point"] as Number).toInt(),
                            ProviderType.valueOf(userData["provider"] as String),
                            (userData["level"] as Number).toInt(),
                            manager.getUserImages(userData["email"] as String),
                            (userData["completeLevel"] as Number).toInt()
                        )
                        println("LISTAAAAAA ${friends}")
                        if (!friends.contains(user.email)) {
                            userList.add(
                                user
                            )
                        }
                        state.value = FriendState.AddFriend
                    }
                }
            } else {
                println("Error al obtener documentos: ${task.exception}")
            }
        }
    }

    fun getFilterList(s: String): MutableList<User> {
        val lista = mutableListOf<User>()
        userList.forEach {
            if (it.email.uppercase().contains(s.uppercase())) {
                lista.add(it)
            }
        }
        return lista
    }

    fun addFriend(user: User) {
        val usersRef = dataBase.collection("users")
        usersRef.document(Locator.email).get().addOnSuccessListener {
            val friends: MutableList<String> = (it.get("friends") as List<String>).toMutableList()
            friends.add(user.email)
            val user = User(
                it.get("email") as String,
                it.get("name") as String,
                friends,
                (it.get("point") as Number).toInt(),
                ProviderType.valueOf(it.get("provider") as String),
                (it.get("level") as Number).toInt(),
                "",
                (it.get("completeLevel") as Number).toInt(),
            )
            dataBase.collection("users").document(Locator.email).set(
                hashMapOf(
                    "friends" to user.friends,
                    "provider" to user.provider,
                    "email" to user.email,
                    "name" to user.name,
                    "point" to user.point,
                    "level" to user.level,
                    "completeLevel" to user.completeLevel
                )
            ).addOnSuccessListener {
                state.value = FriendState.InsertFriend
            }
        }
    }


}