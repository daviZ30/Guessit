package com.dezeta.guessit.utils

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.dezeta.guessit.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class MyWorker(
    private val context: Context,
    private val params: WorkerParameters,
) :
    CoroutineWorker(context, params) {
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
            Result.success()
        }

    }
}