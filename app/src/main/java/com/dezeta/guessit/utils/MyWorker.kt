package com.dezeta.guessit.utils

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dezeta.guessit.R
import com.dezeta.guessit.domain.Repository.UserManager
import com.dezeta.guessit.domain.entity.ProviderType
import com.dezeta.guessit.domain.entity.User
import com.dezeta.guessit.ui.menu.DailyMenuFragment
import com.dezeta.guessit.ui.menu.ExtraState
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MyWorker(
    private val context: Context,
    private val params: WorkerParameters,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val btnType = params.inputData.getString("btnType")
        return withContext(Dispatchers.IO) {
            val fragment = DailyMenuFragment()
            FirebaseFirestore.getInstance().collection("users").document(Locator.email).get()
                .addOnSuccessListener {
                    val user = User(
                        it.get("email") as String,
                        it.get("name") as String,
                        it.get("friends") as List<String>,
                        (it.get("point") as Number).toInt(),
                        ProviderType.valueOf(it.get("provider") as String),
                        (it.get("level") as Number).toInt(),
                        "",
                        (it.get("completeLevel") as Number).toInt(),
                        (it.get("countryEnable") as Boolean),
                        (it.get("serieEnable") as Boolean),
                        (it.get("footballEnable") as Boolean),
                        (it.get("statCountry") as Number).toInt(),
                        (it.get("statSerie") as Number).toInt(),
                        (it.get("statFootball") as Number).toInt(),
                        )
                    when (btnType) {
                        "country" -> {
                            //btnCountry.isEnabled = true
                            user.countryEnable = true
                            Locator.userManager.saveUser(user)
                        }
                        "serie" -> {
                            // btnSerie.isEnabled = true
                            user.serieEnable = true
                            Locator.userManager.saveUser(user)
                        }

                        "football" -> {
                            // btnFootball.isEnabled = true
                            user.footballEnable = true
                            Locator.userManager.saveUser(user)
                        }
                    }
                }
            //24 * 60 * 60 * 1000

            if (Locator.managerFragment != null) {
                Locator.managerFragment!!.beginTransaction()
                    .replace(R.id.layoutDailyMenu, fragment)
                    .commit()
            }

            Result.success()
        }

    }
}