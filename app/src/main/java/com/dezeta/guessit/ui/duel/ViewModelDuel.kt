package com.dezeta.guessit.ui.duel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dezeta.guessit.domain.Repository.Repository
import com.dezeta.guessit.domain.entity.Img
import com.dezeta.guessit.domain.entity.Info
import com.dezeta.guessit.domain.entity.Guess
import com.dezeta.guessit.domain.entity.ProviderType
import com.dezeta.guessit.domain.entity.User
import com.dezeta.guessit.utils.Locator
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class ViewModelDuel : ViewModel() {
    var dataBase = FirebaseFirestore.getInstance()
    var score = MutableLiveData<String>()
    var previousNum: Int? = null
    var point = 0
    fun getSerie(): Guess {
        val lista = Repository.getSeriesList()
        var r: Int
        do {
            r = Random.nextInt(lista.size)
        } while (r == previousNum)
        previousNum = r
        return lista[r]
    }
    fun updatePoint() {
        val email = Locator.email
        dataBase.collection("users").document(email).get().addOnSuccessListener {
            val p = (it.get("point") as Number).toInt() + point
            val user = User(
                it.get("email") as String, p,
                ProviderType.valueOf(it.get("provider") as String),
            )
            dataBase.collection("users").document(user.email).set(
                hashMapOf(
                    "provider" to user.provider,
                    "email" to user.email,
                    "point" to user.point
                )
            )
        }
    }

    fun getImage0(serie: Guess): Img? {
        return Repository.getImage0(serie.id)
    }

    fun getInfoFromId(serie: Guess): Info {
        return Repository.getInfoFromId(serie.id)
    }

}