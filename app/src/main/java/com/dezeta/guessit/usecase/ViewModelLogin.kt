package com.dezeta.guessit.usecase

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dezeta.guessit.domain.Repository.Resource
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception

class ViewModelLogin : ViewModel() {
    var mail = MutableLiveData<String>()
    var password = MutableLiveData<String>()
    var confPassword = MutableLiveData<String>()
    private var state = MutableLiveData<LoginState>()
    private var result = MutableLiveData<Resource>()
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

    fun signup() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
            mail.value!!,
            password.value!!
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                println("IR A CASA VIEWMODEL")
                result.value = Resource.Success(it.result?.user?.email ?: "")
            } else {
                println("ERROR ---------------")
                result.value = Resource.Error(Exception("Se ha producido un error registrando al usuario"))
            }
        }

    }
    fun signin(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
            mail.value!!,
            password.value!!
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                result.value = Resource.Success(it.result?.user?.email ?: "")
            } else {
                result.value = Resource.Error(Exception("Se ha producido un error autenticando al usuario"))
            }
        }
    }
    fun validateSignUp(){
        when {
            TextUtils.isEmpty(mail.value) -> state.value = LoginState.emailEmtyError
            TextUtils.isEmpty(password.value) -> state.value = LoginState.passwordEmtyError
            !validarEmail(mail.value!!) -> state.value = LoginState.emailFormatError
            !validarPassword(password.value!!) -> state.value = LoginState.passwordFormatError
            !(confPassword.value == password.value) -> state.value = LoginState.NotEqualsPasswordError
            else -> state.value = LoginState.Success
        }
    }

    fun validateSignIn() {
        when {
            TextUtils.isEmpty(mail.value) -> state.value = LoginState.emailEmtyError
            TextUtils.isEmpty(password.value) -> state.value = LoginState.passwordEmtyError
            !validarEmail(mail.value!!) -> state.value = LoginState.emailFormatError
            !validarPassword(password.value!!) -> state.value = LoginState.passwordFormatError
            else -> state.value = LoginState.Success
        }
    }

}