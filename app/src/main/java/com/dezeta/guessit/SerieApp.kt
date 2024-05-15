package com.dezeta.guessit
import android.app.Application
import com.dezeta.guessit.utils.Locator

class SerieApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Locator.initWith(this)
    }
}