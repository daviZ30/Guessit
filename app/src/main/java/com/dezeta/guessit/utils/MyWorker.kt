package com.dezeta.guessit.utils

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dezeta.guessit.R
import com.dezeta.guessit.ui.menu.DailyMenuFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MyWorker(
    private val context: Context,
    private val params: WorkerParameters,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val btnType = params.inputData.getString("btnType")
        return withContext(Dispatchers.IO) {
            //24 * 60 * 60 * 1000
            val editPreferences = context.getSharedPreferences(
                context.getString(R.string.prefs_file),
                Context.MODE_PRIVATE
            ).edit()
            when (btnType) {
                "country" -> {
                    //btnCountry.isEnabled = true

                    editPreferences.putBoolean("country", true)
                    editPreferences.apply()
                }

                "serie" -> {
                    // btnSerie.isEnabled = true
                    editPreferences.putBoolean("serie", true)
                    editPreferences.apply()
                }

                "football" -> {
                    // btnFootball.isEnabled = true
                    editPreferences.putBoolean("football", true)
                    editPreferences.apply()
                }
            }
            val fragment = DailyMenuFragment()

            if (Locator.managerFragment != null) {
                Locator.managerFragment!!.beginTransaction()
                    .replace(R.id.layoutDailyMenu, fragment)
                    .commit()
            }

            Result.success()
        }

    }
}