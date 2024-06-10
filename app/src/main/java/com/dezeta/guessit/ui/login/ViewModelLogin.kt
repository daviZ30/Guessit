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
import com.dezeta.guessit.domain.Repository.UserManager
import com.dezeta.guessit.utils.Locator
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class ViewModelLogin : ViewModel() {
    var mail = MutableLiveData<String>()
    var name = MutableLiveData<String>()
    var dialogName = MutableLiveData<String>()
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


    fun saveUser(u: User) {
        viewModelScope.launch(Dispatchers.IO) {
            Locator.userManager.saveUser(u)
        }
    }

    fun signup(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val resu = Locator.userManager.SignUp(mail.value!!, password.value!!, name.value!!, uri)
            when (resu) {
                is Resource.Error -> {
                    withContext(Dispatchers.Main) {
                        result.value = Resource.Error(resu.exception)
                    }
                }

                is Resource.Success<*> -> {
                    withContext(Dispatchers.Main) {
                        result.value = Resource.Success(resu.data as String)
                    }
                }
            }
        }
    }

    fun signin() {
        viewModelScope.launch(Dispatchers.IO) {
            val resu = Locator.userManager.SignIn(mail.value!!, password.value!!)
            when (resu) {
                is Resource.Error -> {
                    val isGoogle = Locator.userManager.isGoogleAccount(mail.value!!)
                    withContext(Dispatchers.Main) {
                        when (isGoogle) {
                            is Resource.Success<*> -> {
                                state.value = LoginState.GoogleSignInError
                            }

                            is Resource.Error -> {
                                result.value = isGoogle
                            }
                        }
                    }

                }

                is Resource.Success<*> -> {
                    val log = resu.data as AuthResult
                    withContext(Dispatchers.Main) {
                        if (!log.user!!.isEmailVerified)
                            state.value = LoginState.EmailNotVerifiedError
                        else {
                            result.value = Resource.Success(
                                log.user?.email ?: ""
                            )
                        }
                    }
                }
            }
        }
        /*FirebaseAuth.getInstance().signInWithEmailAndPassword(
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
        }*/
    }

    fun validateSignUp() {
        viewModelScope.launch(Dispatchers.Default) {
            val result = Locator.userManager.getDocuments()
            if (result is Resource.Success<*>) {
                val task = result.data as QuerySnapshot
                userList.clear()
                for (document in task) {
                    val userData = document.data
                    val user = User(
                        userData[UserManager.EMAIL] as String,
                        userData[UserManager.NAME] as String,
                        userData[UserManager.FRIENDS] as List<String>,
                        (userData[UserManager.POINT] as Number).toInt(),
                        ProviderType.valueOf(userData[UserManager.PROVIDER] as String),
                        (userData[UserManager.LEVEL] as Number).toInt(),
                        "",
                        (userData[UserManager.COMPLETE_LEVEL] as Number).toInt(),
                        userData[UserManager.COUNTRY_ENABLE] as Boolean,
                        userData[UserManager.SERIE_ENABLE] as Boolean,
                        userData[UserManager.FOOTBALL_ENABLE] as Boolean,
                        (userData[UserManager.STAT_COUNTRY] as Number).toInt(),
                        (userData[UserManager.STAT_SERIE] as Number).toInt(),
                        (userData[UserManager.STAT_FOOTBALL] as Number).toInt(),
                    )
                    userList.add(
                        user
                    )
                }
                withContext(Dispatchers.Main) {
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
            userList.clear()
            val resu = Locator.userManager.SignInGoogle(credential)
            when (resu) {
                is Resource.Error -> {
                    withContext(Dispatchers.Main) {
                        withContext(Dispatchers.Main) {
                            result.value =
                                Resource.Error(Exception("Se ha producido un error autenticando al usuario"))
                        }
                    }

                }

                is Resource.Success<*> -> {
                    userList = resu.data as MutableList<User>
                    var exists = false
                    var existsName = false
                    userList.forEach {
                        if (it.email == e) {
                            exists = true
                        }
                        if (it.name == n) {
                            existsName = true
                        }
                    }
                    when {
                        !exists && !existsName -> {
                            user = User(
                                e!!,
                                n,
                                listOf(e),
                                0,
                                ProviderType.GOOGLE,
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
                            saveUser(user!!)
                            withContext(Dispatchers.Main) {
                                state.value = LoginState.GoogleSuccess(e)
                            }
                        }

                        !exists && existsName -> {
                            user = User(
                                e!!,
                                n,
                                listOf(e),
                                0,
                                ProviderType.GOOGLE,
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
                            saveUser(user!!)
                            withContext(Dispatchers.Main) {
                                state.value = LoginState.GoogleNameExists
                            }
                        }

                        else -> {
                            withContext(Dispatchers.Main) {
                                state.value = LoginState.GoogleSuccess(e!!)
                            }
                        }
                    }
                }

                else -> {
                    withContext(Dispatchers.Main) {
                        result.value =
                            Resource.Error(Exception("Se ha producido un error NULL"))
                    }
                }
            }
        }
    }

    fun validateDialogName() {
        viewModelScope.launch(Dispatchers.Default) {
            val res = Locator.userManager.getDocuments()
            if (res is Resource.Success<*>) {
                userList.clear()
                val task =  res.data as QuerySnapshot
                for (document in task) {
                    val userData = document.data
                    val user = User(
                        userData[UserManager.EMAIL] as String,
                        userData[UserManager.NAME] as String,
                        userData[UserManager.FRIENDS] as List<String>,
                        (userData[UserManager.POINT] as Number).toInt(),
                        ProviderType.valueOf(userData[UserManager.PROVIDER] as String),
                        (userData[UserManager.LEVEL] as Number).toInt(),
                        "",
                        (userData[UserManager.COMPLETE_LEVEL] as Number).toInt(),
                        userData[UserManager.COUNTRY_ENABLE] as Boolean,
                        userData[UserManager.SERIE_ENABLE] as Boolean,
                        userData[UserManager.FOOTBALL_ENABLE] as Boolean,
                        (userData[UserManager.STAT_COUNTRY] as Number).toInt(),
                        (userData[UserManager.STAT_SERIE] as Number).toInt(),
                        (userData[UserManager.STAT_FOOTBALL] as Number).toInt(),
                    )
                    userList.add(
                        user
                    )
                }
                when {
                    TextUtils.isEmpty(dialogName.value) -> state.value =
                        LoginState.GoogleNameEmpty

                    !validarName(dialogName.value, userList) -> state.value =
                        LoginState.GoogleNameEquals

                    else -> {
                        user!!.name = dialogName.value.toString()
                        saveUser(user!!)
                        state.value = LoginState.GoogleSuccess(user!!.email)
                    }
                }

            }
        }
    }
}