package com.dezeta.guessit.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dezeta.guessit.domain.entity.User
import com.google.firebase.firestore.FirebaseFirestore

class ViewModelMain : ViewModel() {

    var user = MutableLiveData<User>()

}