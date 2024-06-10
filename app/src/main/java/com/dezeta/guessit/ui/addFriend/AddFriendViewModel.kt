package com.dezeta.guessit.ui.addFriend

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezeta.guessit.domain.Repository.Resource
import com.dezeta.guessit.domain.Repository.UserManager
import com.dezeta.guessit.domain.entity.ProviderType
import com.dezeta.guessit.domain.entity.User
import com.dezeta.guessit.ui.friend.FriendState
import com.dezeta.guessit.utils.CloudStorageManager
import com.dezeta.guessit.utils.Locator
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

            getAllUserAccounts()
        }

    }

    fun getState(): LiveData<FriendState> {
        return state
    }


    private fun getAllUserAccounts() {
        viewModelScope.launch(Dispatchers.Default) {
            val result = Locator.userManager.getDocuments()
            if (result is Resource.Success<*>) {
                val task = result.data as QuerySnapshot
                userList.clear()
                val allList = mutableListOf<User>()
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
                    allList.add(
                        user
                    )
                }
                for (u in allList) {
                    if (!friends.contains(u.email)) {
                        userList.add(
                            u
                        )
                    }
                    withContext(Dispatchers.Main) {
                        state.value = FriendState.AddFriend
                    }
                }

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
        viewModelScope.launch(Dispatchers.IO) {
            val result = Locator.userManager.addFriend(user.email)
            if (result is Resource.Success<*>)
                withContext(Dispatchers.Main) {
                    state.value = FriendState.InsertFriend
                }
        }
    }
}