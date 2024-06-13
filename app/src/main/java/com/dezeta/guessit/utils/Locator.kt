package com.dezeta.guessit.utils

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.room.util.appendPlaceholders
import com.dezeta.guessit.domain.Repository.UserManager
import com.example.signup.data.userPreferences.PreferencesRepository
import com.github.nikartm.button.FitButton
import com.google.firebase.firestore.FirebaseFirestore

object Locator {
    public var application: Application? = null
    var ImagePath = "/data/data/com.dezeta.guessit/app_imageDir/"
    var email = ""
    val OFFLINE_MODE = "OFFLINE_MODE"
    var managerFragment: FragmentManager? = null

    //inline, Cuando la llamas se inicializa y te lo da
    public inline val requiredApplication
        get() = application ?: error("Missing call: initwith(application)")
    private val Context.userStore by preferencesDataStore(name = "user_preferences")

    val PreferencesRepository: PreferencesRepository by lazy {
        PreferencesRepository(requiredApplication.userStore)
    }

    fun initWith(application: Application) {
        this.application = application
        NetworkConnection.initialize(application)
    }

    val userManager by lazy {
        UserManager()
    }
}