package com.dezeta.guessit.ui.friend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezeta.guessit.domain.Repository.Resource
import com.dezeta.guessit.domain.entity.ProviderType
import com.dezeta.guessit.domain.entity.User
import com.dezeta.guessit.utils.CloudStorageManager
import com.dezeta.guessit.utils.Locator
import com.dezeta.guessit.domain.Repository.UserManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FriendViewModel : ViewModel() {
    private var state = MutableLiveData<FriendState>()
    var userList: MutableList<User> = mutableListOf()
    fun getState(): LiveData<FriendState> {
        return state
    }

    fun loadUser(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = Locator.userManager.loadUser(email)
            if (result is Resource.Success<*>) {
                val user = result.data as User
                userList.add(user)
                withContext(Dispatchers.Main) {
                    state.value = FriendState.AddFriend
                }
            }
        }
    }

    fun getAllFriend() {
        viewModelScope.launch(Dispatchers.Default) {
            val result = Locator.userManager.getFriend()
            if (result is Resource.Success<*>) {
                val friends = result.data as List<String>
                for (f in friends) {
                    loadUser(f)
                }
            }
        }
    }

    fun removeFriend(user: User) {
        userList.remove(user)
        viewModelScope.launch(Dispatchers.IO) {
            Locator.userManager.removeFriend(user)
        }
    }
}