package com.dezeta.guessit.utils

import android.app.Application
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import com.dezeta.guessit.domain.Repository.UserManager
import com.github.nikartm.button.FitButton
import com.google.firebase.firestore.FirebaseFirestore

object Locator {
    public var application: Application? = null
    var ImagePath = "/data/data/com.dezeta.guessit/app_imageDir/"
    var email = ""
    var managerFragment :FragmentManager? = null
    //inline, Cuando la llamas se inicializa y te lo da
    public inline val requiredApplication
        get() = application ?: error("Missing call: initwith(application)")

    fun initWith(application: Application) {
        this.application = application
    }
    val userManager by lazy {
        UserManager()
    }
}