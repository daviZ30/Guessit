package com.dezeta.guessit.ui.test

import androidx.lifecycle.ViewModel
import com.dezeta.guessit.domain.Repository.Repository
import com.dezeta.guessit.domain.Repository.Resource
import com.dezeta.guessit.domain.entity.AnswerTest
import com.dezeta.guessit.domain.entity.Guess
import com.dezeta.guessit.domain.entity.Img
import com.dezeta.guessit.domain.entity.ProviderType
import com.dezeta.guessit.domain.entity.User
import com.dezeta.guessit.utils.Locator
import com.dezeta.guessit.utils.UserManager
import com.google.firebase.firestore.FirebaseFirestore

class ViewModelTest : ViewModel() {
    var dataBase = FirebaseFirestore.getInstance()
    var tests = mutableListOf<Guess>()
    lateinit var img0: Img
    var answers0: List<AnswerTest>? = null
    lateinit var img1: Img
    var answers1: List<AnswerTest>? = null
    lateinit var img2: Img
    var answers2: List<AnswerTest>? = null
    var local = false
    var point = 0;

    fun getImageUrl(guess: Guess): Img? {
        return when (val result = Repository.getImageFromId(guess.id)) {
            is Resource.Error -> null
            is Resource.Success<*> -> (result.data as List<Img>)[0]
        }
    }

    fun getAnswers(guess: Guess): List<AnswerTest>? {
        return when (val result = Repository.getAnswerFromId(guess.id)) {
            is Resource.Error -> null
            is Resource.Success<*> -> result.data as List<AnswerTest>
        }
    }

    fun updatePoint() {
       UserManager.UpdatePoint(point)
    }
    fun updateLevel(level:Int) {
        UserManager.UpdateCompleteLevel(level)
    }
}