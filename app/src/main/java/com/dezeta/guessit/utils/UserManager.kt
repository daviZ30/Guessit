package com.dezeta.guessit.utils

import com.dezeta.guessit.domain.entity.ProviderType
import com.dezeta.guessit.domain.entity.User
import com.google.firebase.firestore.FirebaseFirestore

class UserManager {
    companion object {
        fun saveUser(user: User) {
            val dataBase = FirebaseFirestore.getInstance()
            dataBase.collection("users").document(user.email).set(
                hashMapOf(
                    "friends" to user.friends,
                    "provider" to user.provider,
                    "email" to user.email,
                    "name" to user.name,
                    "point" to user.point,
                    "level" to user.level,
                    "completeLevel" to user.completeLevel
                )
            )
        }

        fun UpdateFriends(list: List<String>) {
            val dataBase = FirebaseFirestore.getInstance()
            dataBase.collection("users").document(Locator.email).get().addOnSuccessListener {
                val user = User(
                    it.get("email") as String,
                    it.get("name") as String,
                    list,
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
                )
            }


        }

        fun UpdateCompleteLevel(level: Int) {
            val dataBase = FirebaseFirestore.getInstance()
            val email = Locator.email
            dataBase.collection("users").document(email).get().addOnSuccessListener {
                val user = User(
                    it.get("email") as String,
                    it.get("name") as String,
                    it.get("friends") as List<String>,
                    (it.get("point") as Number).toInt(),
                    ProviderType.valueOf(it.get("provider") as String),
                    (it.get("level") as Number).toInt(),
                    "",
                    level,
                )
                if (level >= (it.get("completeLevel") as Number).toInt()) {
                    dataBase.collection("users").document(user.email).set(
                        hashMapOf(
                            "friends" to user.friends,
                            "provider" to user.provider,
                            "email" to user.email,
                            "name" to user.name,
                            "point" to user.point,
                            "level" to user.level,
                            "completeLevel" to user.completeLevel
                        )
                    )
                }
            }
        }

        fun UpdatePoint(point: Int) {
            val dataBase = FirebaseFirestore.getInstance()
            val email = Locator.email
            dataBase.collection("users").document(email).get().addOnSuccessListener {
                val p = (it.get("point") as Number).toInt() + point
                val user = User(
                    it.get("email") as String,
                    it.get("name") as String,
                    it.get("friends") as List<String>,
                    p,
                    ProviderType.valueOf(it.get("provider") as String),
                    (it.get("level") as Number).toInt(),
                    "",
                    (it.get("completeLevel") as Number).toInt(),
                )
                dataBase.collection("users").document(user.email).set(
                    hashMapOf(
                        "friends" to user.friends,
                        "provider" to user.provider,
                        "email" to user.email,
                        "name" to user.name,
                        "point" to user.point,
                        "level" to user.level,
                        "completeLevel" to user.completeLevel
                    )
                )
            }
        }

        fun UpdateUser(user: User) {
            val dataBase = FirebaseFirestore.getInstance()
            dataBase.collection("users").document(user.email).set(
                hashMapOf(
                    "friends" to user.friends,
                    "provider" to user.provider,
                    "email" to user.email,
                    "name" to user.name,
                    "point" to user.point,
                    "level" to user.level,
                    "completeLevel" to user.completeLevel
                )
            )
        }

    }
}