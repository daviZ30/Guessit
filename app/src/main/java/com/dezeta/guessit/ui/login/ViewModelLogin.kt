package com.dezeta.guessit.ui.login

import android.net.Uri
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezeta.guessit.domain.Repository.Resource
import com.dezeta.guessit.domain.entity.ProviderType
import com.dezeta.guessit.domain.entity.User
import com.dezeta.guessit.utils.UserManager
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class ViewModelLogin : ViewModel() {
    var dataBase = FirebaseFirestore.getInstance()
    var mail = MutableLiveData<String>()
    var name = MutableLiveData<String>()
    var password = MutableLiveData<String>()
    var confPassword = MutableLiveData<String>()
    private var state = MutableLiveData<LoginState>()
    private var result = MutableLiveData<Resource>()
    var user: User? = null
    var userList: MutableList<User> = mutableListOf()


    fun getState(): LiveData<LoginState> {
        return state
    }

    fun getResult(): LiveData<Resource> {
        return result
    }

    fun validarEmail(e: String): Boolean {
        val regex = Regex("^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")
        return regex.matches(e)
    }

    fun validarPassword(p: String): Boolean {
        return p.any { it.isDigit() } && p.any { it.isLetter() } && p.length >= 8
    }

    fun saveImageProfile(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val storage = FirebaseStorage.getInstance()
            val storageReference = storage.reference.child("images/${mail.value}_img1.jpg")

            val uploadTask = storageReference.putFile(uri)

            uploadTask.addOnSuccessListener { taskSnapshot ->
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    //getUserProfileImageByEmail()
                }.addOnFailureListener { exception ->
                    println("ERROR. $exception")
                }
            }.addOnFailureListener { exception ->
                println("ERROR. $exception")
            }
        }
    }


    fun saveUser(u: User) {
        viewModelScope.launch(Dispatchers.IO) {
            UserManager.saveUser(u)
        }
    }

    fun signup(uri: Uri) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
            mail.value!!,
            password.value!!
        ).addOnCompleteListener { r ->
            if (r.isSuccessful) {
                saveImageProfile(uri)
                FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                user = User(
                    r.result?.user?.email ?: "",
                    name.value!!,
                    listOf(r.result?.user?.email ?: ""),
                    0,
                    ProviderType.BASIC,
                    0,
                    "",
                    0
                )
                saveUser(user!!)
                result.value = Resource.Success(
                    user!!.email
                )
            } else {
                result.value =
                    Resource.Error(Exception("Su cuenta de correo ya esta registrada"))
            }
        }

    }

    fun signin() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
            mail.value!!,
            password.value!!
        ).addOnCompleteListener { r ->
            if (r.isSuccessful) {
                if (!r.result.user!!.isEmailVerified)
                    state.value = LoginState.EmailNotVerifiedError
                else {
                    result.value = Resource.Success(
                        r.result?.user?.email ?: ""
                    )
                }

            } else {
                dataBase.collection("users").document(mail.value!!).get().addOnSuccessListener {
                    if (ProviderType.valueOf(it.get("provider") as String) == ProviderType.GOOGLE) {
                        state.value = LoginState.GoogleSignInError
                    }
                }.addOnFailureListener {
                    result.value =
                        Resource.Error(Exception("Se ha producido un error autenticando al usuario, registrelo antes de iniciar sesión"))
                }
            }
        }
    }

    fun validateSignUp() {
        val usersRef = dataBase.collection("users")
        viewModelScope.launch(Dispatchers.Default) {
            usersRef.get().addOnCompleteListener { task ->
                userList.clear()
                if (task.isSuccessful) {
                    for (document in task.result) {
                        val userData = document.data
                        val user = User(
                            userData["email"] as String,
                            userData["name"] as String,
                            userData["friends"] as List<String>,
                            (userData["point"] as Number).toInt(),
                            ProviderType.valueOf(userData["provider"] as String),
                            (userData["level"] as Number).toInt(),
                            "",
                            (userData["completeLevel"] as Number).toInt()
                        )
                        userList.add(
                            user
                        )
                    }
                    when {
                        TextUtils.isEmpty(mail.value) -> state.value = LoginState.emailEmtyError
                        TextUtils.isEmpty(password.value) -> state.value =
                            LoginState.passwordEmtyError

                        TextUtils.isEmpty(name.value) -> state.value = LoginState.nameEmtyError
                        !validarName(name.value, userList) -> state.value =
                            LoginState.nameEqualsError

                        !validarEmail(mail.value!!) -> state.value = LoginState.emailFormatError
                        !validarPassword(password.value!!) -> state.value =
                            LoginState.passwordFormatError

                        confPassword.value != password.value -> state.value =
                            LoginState.NotEqualsPasswordError

                        else -> {
                            state.value = LoginState.Success

                        }
                    }
                } else {
                    println("Error al obtener documentos: ${task.exception}")
                }
            }
        }


    }

    private fun validarName(value: String?, userList: MutableList<User>): Boolean {
        var bool = true
        userList.forEach {
            if (it.name == value)
                bool = false
        }
        return bool
    }

    fun validateSignIn() {
        when {
            TextUtils.isEmpty(mail.value) -> state.value = LoginState.emailEmtyError
            TextUtils.isEmpty(password.value) -> state.value = LoginState.passwordEmtyError
            !validarEmail(mail.value!!) -> state.value = LoginState.emailFormatError
            else -> state.value = LoginState.Success
        }
    }

    fun signInGoogle(credential: AuthCredential, e: String?, n: String) {
        viewModelScope.launch(Dispatchers.Default) {
            FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                if (it.isSuccessful) {
                    val usersRef = dataBase.collection("users")
                    usersRef.get().addOnCompleteListener { task ->
                        userList.clear()
                        if (task.isSuccessful) {
                            for (document in task.result) {
                                val userData = document.data
                                val user = User(
                                    userData["email"] as String,
                                    userData["name"] as String,
                                    userData["friends"] as List<String>,
                                    (userData["point"] as Number).toInt(),
                                    ProviderType.valueOf(userData["provider"] as String),
                                    (userData["level"] as Number).toInt(),
                                    "",
                                    (userData["completeLevel"] as Number).toInt()
                                )
                                userList.add(
                                    user
                                )
                            }
                            var exists = false
                            userList.forEach {
                                if (it.email == e) {
                                    exists = true
                                }
                            }
                            if (!exists) {
                                val u = User(
                                    e!!,
                                    n,
                                    listOf(e),
                                    0,
                                    ProviderType.GOOGLE,
                                    0,
                                    "",
                                    0
                                )
                                saveUser(u)
                                state.value = LoginState.GoogleSuccess(e)
                            } else {
                                state.value = LoginState.GoogleSuccess(e!!)
                            }
                        } else {
                            println("Error al obtener documentos: ${task.exception}")
                        }
                    }


                } else {
                    result.value =
                        Resource.Error(Exception("Se ha producido un error autenticando al usuario"))
                }
            }
        }

    }


}