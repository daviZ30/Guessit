package com.dezeta.guessit.domain.Repository

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.dezeta.guessit.domain.entity.ProviderType
import com.dezeta.guessit.domain.entity.User
import com.dezeta.guessit.ui.main.MainState
import com.dezeta.guessit.utils.CloudStorageManager
import com.dezeta.guessit.utils.Locator
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserManager {
    private val dataBase = FirebaseFirestore.getInstance()
    val usersRef = dataBase.collection("users")
    private var manager = CloudStorageManager()

    fun saveUser(user: User) {
        dataBase.collection("users").document(user.email).set(
            hashMapOf(
                FRIENDS to user.friends,
                PROVIDER to user.provider,
                EMAIL to user.email,
                NAME to user.name,
                POINT to user.point,
                LEVEL to user.level,
                COMPLETE_LEVEL to user.completeLevel,
                COUNTRY_ENABLE to user.countryEnable,
                SERIE_ENABLE to user.serieEnable,
                FOOTBALL_ENABLE to user.footballEnable,
                STAT_COUNTRY to user.statCountry,
                STAT_SERIE to user.statSerie,
                STAT_FOOTBALL to user.statFootball
            )
        )
    }

    suspend fun getUserList(): Resource {
        return try {
            val task = usersRef.get().await()
            val userList = mutableListOf<User>()
            for (document in task) {
                val userData = document.data
                val user = User(
                    userData[EMAIL] as String,
                    userData[NAME] as String,
                    userData[FRIENDS] as List<String>,
                    (userData[POINT] as Number).toInt(),
                    ProviderType.valueOf(userData[UserManager.PROVIDER] as String),
                    (userData[LEVEL] as Number).toInt(),
                    manager.getUserImages(userData[UserManager.EMAIL] as String),
                    (userData[COMPLETE_LEVEL] as Number).toInt(),
                    (userData[COUNTRY_ENABLE] as Boolean),
                    (userData[SERIE_ENABLE] as Boolean),
                    (userData[FOOTBALL_ENABLE] as Boolean),
                    (userData[STAT_COUNTRY] as Number).toInt(),
                    (userData[STAT_SERIE] as Number).toInt(),
                    (userData[STAT_FOOTBALL] as Number).toInt(),
                )
                userList.add(
                    user
                )
            }
            Resource.Success(userList)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    suspend fun getDocuments(): Resource {
        return try {
            val task = usersRef.get().await()
            Resource.Success(task)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    suspend fun getFriend(): Resource {
        try {
            val document = usersRef.document(Locator.email).get().await()
            val friends: List<String> = (document.get("friends") as List<String>)
            return Resource.Success(friends)
        } catch (e: Exception) {
            return Resource.Error(e)
        }

    }

    fun UpdateFriends(list: List<String>) {
        dataBase.collection("users").document(Locator.email).get().addOnSuccessListener {
            val user = User(
                it.get(EMAIL) as String,
                it.get(NAME) as String,
                list,
                (it.get(POINT) as Number).toInt(),
                ProviderType.valueOf(it.get("provider") as String),
                (it.get(LEVEL) as Number).toInt(),
                "",
                (it.get(COMPLETE_LEVEL) as Number).toInt(),
                (it.get(COUNTRY_ENABLE) as Boolean),
                (it.get(SERIE_ENABLE) as Boolean),
                (it.get(FOOTBALL_ENABLE) as Boolean),
                (it.get(STAT_COUNTRY) as Number).toInt(),
                (it.get(STAT_SERIE) as Number).toInt(),
                (it.get(STAT_FOOTBALL) as Number).toInt(),
            )
            dataBase.collection("users").document(Locator.email).set(
                hashMapOf(
                    FRIENDS to user.friends,
                    PROVIDER to user.provider,
                    EMAIL to user.email,
                    NAME to user.name,
                    POINT to user.point,
                    LEVEL to user.level,
                    COMPLETE_LEVEL to user.completeLevel,
                    COUNTRY_ENABLE to user.countryEnable,
                    SERIE_ENABLE to user.serieEnable,
                    FOOTBALL_ENABLE to user.footballEnable,
                    STAT_COUNTRY to user.statCountry,
                    STAT_SERIE to user.statSerie,
                    STAT_FOOTBALL to user.statFootball
                )
            )
        }
    }

    fun deleteUser(email: String) {
        dataBase.collection("users").document(email).delete().addOnSuccessListener {
            FirebaseAuth.getInstance().currentUser!!.delete()
        }
    }

    suspend fun addFriend(friend: String): Resource {
        try {
            val document = usersRef.document(Locator.email).get().await()
            val friends: MutableList<String> =
                (document.get(FRIENDS) as List<String>).toMutableList()
            friends.add(friend)
            val user = User(
                document.get(EMAIL) as String,
                document.get(NAME) as String,
                friends,
                (document.get(POINT) as Number).toInt(),
                ProviderType.valueOf(document.get("provider") as String),
                (document.get(LEVEL) as Number).toInt(),
                "",
                (document.get(COMPLETE_LEVEL) as Number).toInt(),
                (document.get(COUNTRY_ENABLE) as Boolean),
                (document.get(SERIE_ENABLE) as Boolean),
                (document.get(FOOTBALL_ENABLE) as Boolean),
                (document.get(STAT_COUNTRY) as Number).toInt(),
                (document.get(STAT_SERIE) as Number).toInt(),
                (document.get(STAT_FOOTBALL) as Number).toInt()
            )
            dataBase.collection("users").document(Locator.email).set(
                hashMapOf(
                    FRIENDS to user.friends,
                    PROVIDER to user.provider,
                    EMAIL to user.email,
                    NAME to user.name,
                    POINT to user.point,
                    LEVEL to user.level,
                    COMPLETE_LEVEL to user.completeLevel,
                    COUNTRY_ENABLE to user.countryEnable,
                    SERIE_ENABLE to user.serieEnable,
                    FOOTBALL_ENABLE to user.footballEnable,
                    STAT_COUNTRY to user.statCountry,
                    STAT_SERIE to user.statSerie,
                    STAT_FOOTBALL to user.statFootball
                )
            ).await()
            return Resource.Success(null)
        } catch (exception: Exception) {
            return Resource.Error(exception)
        }
    }

    fun UpdateCompleteLevel(level: Int) {
        val email = Locator.email
        dataBase.collection("users").document(email).get().addOnSuccessListener {
            val user = User(
                it.get(EMAIL) as String,
                it.get(NAME) as String,
                it.get(FRIENDS) as List<String>,
                (it.get(POINT) as Number).toInt(),
                ProviderType.valueOf(it.get("provider") as String),
                (it.get(LEVEL) as Number).toInt(),
                "",
                level,
                (it.get(COUNTRY_ENABLE) as Boolean),
                (it.get(SERIE_ENABLE) as Boolean),
                (it.get(FOOTBALL_ENABLE) as Boolean),
                (it.get(STAT_COUNTRY) as Number).toInt(),
                (it.get(STAT_SERIE) as Number).toInt(),
                (it.get(STAT_FOOTBALL) as Number).toInt(),
            )
            if (level >= (it.get("completeLevel") as Number).toInt()) {
                dataBase.collection("users").document(user.email).set(
                    hashMapOf(
                        FRIENDS to user.friends,
                        PROVIDER to user.provider,
                        EMAIL to user.email,
                        NAME to user.name,
                        POINT to user.point,
                        LEVEL to user.level,
                        COMPLETE_LEVEL to user.completeLevel,
                        COUNTRY_ENABLE to user.countryEnable,
                        SERIE_ENABLE to user.serieEnable,
                        FOOTBALL_ENABLE to user.footballEnable,
                        STAT_COUNTRY to user.statCountry,
                        STAT_SERIE to user.statSerie,
                        STAT_FOOTBALL to user.statFootball
                    )
                )
            }
        }
    }

    fun UpdatePoint(point: Int) {
        val email = Locator.email
        dataBase.collection("users").document(email).get().addOnSuccessListener {
            val p = (it.get("point") as Number).toInt() + point
            val user = User(
                it.get(EMAIL) as String,
                it.get(NAME) as String,
                it.get(FRIENDS) as List<String>,
                p,
                ProviderType.valueOf(it.get("provider") as String),
                (it.get(LEVEL) as Number).toInt(),
                "",
                (it.get(COMPLETE_LEVEL) as Number).toInt(),
                (it.get(COUNTRY_ENABLE) as Boolean),
                (it.get(SERIE_ENABLE) as Boolean),
                (it.get(FOOTBALL_ENABLE) as Boolean),
                (it.get(STAT_COUNTRY) as Number).toInt(),
                (it.get(STAT_SERIE) as Number).toInt(),
                (it.get(STAT_FOOTBALL) as Number).toInt(),
            )
            dataBase.collection("users").document(user.email).set(
                hashMapOf(
                    FRIENDS to user.friends,
                    PROVIDER to user.provider,
                    EMAIL to user.email,
                    NAME to user.name,
                    POINT to user.point,
                    LEVEL to user.level,
                    COMPLETE_LEVEL to user.completeLevel,
                    COUNTRY_ENABLE to user.countryEnable,
                    SERIE_ENABLE to user.serieEnable,
                    FOOTBALL_ENABLE to user.footballEnable,
                    STAT_COUNTRY to user.statCountry,
                    STAT_SERIE to user.statSerie,
                    STAT_FOOTBALL to user.statFootball
                )
            )
        }
    }

    suspend fun UpdateUser(user: User): Resource {
        try {
            dataBase.collection("users").document(user.email).set(
                hashMapOf(
                    FRIENDS to user.friends,
                    PROVIDER to user.provider,
                    EMAIL to user.email,
                    NAME to user.name,
                    POINT to user.point,
                    LEVEL to user.level,
                    COMPLETE_LEVEL to user.completeLevel,
                    COUNTRY_ENABLE to user.countryEnable,
                    SERIE_ENABLE to user.serieEnable,
                    FOOTBALL_ENABLE to user.footballEnable,
                    STAT_COUNTRY to user.statCountry,
                    STAT_SERIE to user.statSerie,
                    STAT_FOOTBALL to user.statFootball
                )
            ).await()
            return Resource.Success(null)
        } catch (exception: Exception) {
            return Resource.Error(exception)
        }
    }

    suspend fun removeFriend(user: User) {
        val usersRef = dataBase.collection("users")
        val document = usersRef.document(Locator.email).get().await()
        val friends: MutableList<String> = (document.get("friends") as List<String>).toMutableList()
        friends.remove(user.email)
        UpdateFriends(friends)
    }

    suspend fun loadUser(): Resource {
        try {
            val document = dataBase.collection("users").document(Locator.email).get().await()
            val user = User(
                document.get(EMAIL) as String,
                document.get(NAME) as String,
                document.get(FRIENDS) as List<String>,
                (document.get(POINT) as Number).toInt(),
                ProviderType.valueOf(document.get(PROVIDER) as String),
                (document.get(LEVEL) as Number).toInt(),
                "",
                (document.get(COMPLETE_LEVEL) as Number).toInt(),
                (document.get(COUNTRY_ENABLE) as Boolean),
                (document.get(SERIE_ENABLE) as Boolean),
                (document.get(FOOTBALL_ENABLE) as Boolean),
                (document.get(STAT_COUNTRY) as Number).toInt(),
                (document.get(STAT_SERIE) as Number).toInt(),
                (document.get(STAT_FOOTBALL) as Number).toInt(),
            )
            return Resource.Success(user)
        } catch (e: Exception) {
            return Resource.Error(e)
        }


    }

    suspend fun loadUser(email: String): Resource {
        try {
            val document = dataBase.collection("users").document(email).get().await()
            val user = User(
                document.get(EMAIL) as String,
                document.get(NAME) as String,
                document.get(FRIENDS) as List<String>,
                (document.get(POINT) as Number).toInt(),
                ProviderType.valueOf(document.get(PROVIDER) as String),
                (document.get(LEVEL) as Number).toInt(),
                manager.getUserImages(document.get(EMAIL) as String),
                (document.get(COMPLETE_LEVEL) as Number).toInt(),
                (document.get(COUNTRY_ENABLE) as Boolean),
                (document.get(SERIE_ENABLE) as Boolean),
                (document.get(FOOTBALL_ENABLE) as Boolean),
                (document.get(STAT_COUNTRY) as Number).toInt(),
                (document.get(STAT_SERIE) as Number).toInt(),
                (document.get(STAT_FOOTBALL) as Number).toInt(),
            )
            return Resource.Success(user)
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }

    suspend fun SignInGoogle(credential: AuthCredential): Resource? {
        try {
            FirebaseAuth.getInstance().signInWithCredential(credential).await()
            val userList = mutableListOf<User>()
            val usersRef = dataBase.collection("users")
            val task = usersRef.get().await()
            for (document in task) {
                val userData = document.data
                val user = User(
                    userData["email"] as String,
                    userData["name"] as String,
                    userData["friends"] as List<String>,
                    (userData["point"] as Number).toInt(),
                    ProviderType.valueOf(userData["provider"] as String),
                    (userData["level"] as Number).toInt(),
                    "",
                    (userData["completeLevel"] as Number).toInt(),
                    userData["countryEnable"] as Boolean,
                    userData["serieEnable"] as Boolean,
                    userData["footballEnable"] as Boolean,
                    (userData["statCountry"] as Number).toInt(),
                    (userData["statSerie"] as Number).toInt(),
                    (userData["statFootball"] as Number).toInt(),
                )
                userList.add(
                    user
                )
            }
            return Resource.Success(userList)
        } catch (e: Exception) {
            return Resource.Error(e)
        }

    }

    suspend fun isGoogleAccount(email: String): Resource {
        return try {
            val document = dataBase.collection("users").document(email).get().await()
            if (ProviderType.valueOf(document.get("provider") as String) == ProviderType.GOOGLE) {
                Resource.Success(email)
            } else {
                Resource.Error(Exception("Contraseña incorrecta"))
            }
        } catch (e: Exception) {
            Resource.Error(Exception("Se ha producido un error autenticando al usuario, registrelo antes de iniciar sesión"))
        }
    }

    suspend fun SignUp(email: String, password: String, name: String, uri: Uri): Resource {
        try {
            val log = FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                email,
                password
            ).await()
            saveImageProfile(uri, email)
            FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
            val user = User(
                log.user?.email ?: "",
                name,
                listOf(log.user?.email ?: ""),
                0,
                ProviderType.BASIC,
                0,
                "",
                0,
                true,
                true,
                true,
                0,
                0,
                0
            )
            saveUser(user)
            return Resource.Success(
                user.email
            )
        } catch (e: Exception) {
            return Resource.Error(Exception("Su cuenta de correo ya esta registrada"))
        }
    }

    suspend fun SignIn(email: String, password: String): Resource {
        try {
            val log = FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
            return Resource.Success(log)
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }

    private fun saveImageProfile(uri: Uri, email: String) {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference.child("images/${email}_img1.jpg")

        val uploadTask = storageReference.putFile(uri)

        uploadTask.addOnSuccessListener {
            storageReference.downloadUrl.addOnFailureListener { exception ->
                println("ERROR. $exception")
            }
        }.addOnFailureListener { exception ->
            println("ERROR. $exception")
        }

    }

    suspend fun deleteUser(): Resource {
        return try {
            dataBase.collection("users").document(Locator.email).delete().await()
            FirebaseAuth.getInstance().currentUser!!.delete()
            Resource.Success("")
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    companion object {
        val FRIENDS = "friends"
        val PROVIDER = "provider"
        val EMAIL = "email"
        val NAME = "name"
        val POINT = "point"
        val LEVEL = "level"
        val COMPLETE_LEVEL = "completeLevel"
        val COUNTRY_ENABLE = "countryEnable"
        val SERIE_ENABLE = "serieEnable"
        val FOOTBALL_ENABLE = "footballEnable"
        val STAT_COUNTRY = "statCountry"
        val STAT_SERIE = "statSerie"
        val STAT_FOOTBALL = "statFootball"
    }

}