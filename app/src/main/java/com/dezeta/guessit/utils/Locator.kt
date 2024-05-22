package com.dezeta.guessit.utils

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore

object Locator {
    public var application: Application? = null
    var ImagePath = "/data/data/com.dezeta.guessit/app_imageDir/"


    //inline, Cuando la llamas se inicializa y te lo da
    public inline val requiredApplication
        get() = application ?: error("Missing call: initwith(application)")

    fun initWith(application: Application) {
        this.application = application
    }
}