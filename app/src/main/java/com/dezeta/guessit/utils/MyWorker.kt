package com.dezeta.guessit.utils

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dezeta.guessit.R
import com.dezeta.guessit.domain.Repository.Resource
import com.dezeta.guessit.domain.Repository.UserManager
import com.dezeta.guessit.domain.entity.GuessType
import com.dezeta.guessit.domain.entity.ProviderType
import com.dezeta.guessit.domain.entity.User
import com.dezeta.guessit.ui.daily.DailyState
import com.dezeta.guessit.ui.menu.DailyMenuFragment
import com.dezeta.guessit.ui.menu.ExtraState
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyWorker(
    private val context: Context,
    private val params: WorkerParameters,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val btnType = params.inputData.getString("btnType")

        return  withContext(Dispatchers.IO) {
            val fragment = DailyMenuFragment()
            val result = Locator.userManager.loadUser()
            if (result is Resource.Success<*>) {
                val user = result.data as User
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
            if (Locator.managerFragment != null) {
                Locator.managerFragment!!.beginTransaction()
                    .replace(R.id.layoutDailyMenu, fragment)
                    .commit()
            }
            Result.success()
        }

    }
}