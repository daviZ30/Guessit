package com.dezeta.guessit.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dezeta.guessit.R
import com.dezeta.guessit.domain.Repository.Resource
import com.dezeta.guessit.domain.Repository.UserManager
import com.dezeta.guessit.domain.entity.ProviderType
import com.dezeta.guessit.domain.entity.User
import com.dezeta.guessit.ui.login.LoginActivity
import com.dezeta.guessit.ui.menu.DailyMenuFragment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MyWorker(
    private val context: Context,
    private val params: WorkerParameters,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val email = params.inputData.getString("email")
                val fragment = DailyMenuFragment()
                val result = loadUser(email ?: "")
                if (result is Resource.Success<*>) {
                    val user = result.data as User
                    user.countryEnable = true
                    user.serieEnable = true
                    user.footballEnable = true
                    saveUser(user)
                } else {
                    logError("Error loading user: ${(result as Resource.Error).exception.message}")
                    return@withContext Result.failure()
                }

                if (Locator.managerFragment != null) {
                    Locator.managerFragment!!.beginTransaction()
                        .replace(R.id.layoutDailyMenu, fragment)
                        .commit()
                }

                showNotification(
                    context.getString(R.string.NotTitle),
                    context.getString(R.string.NotMessage)
                )
                Result.success()
            } catch (e: Exception) {
                logError("Exception in doWork: ${e.message}")
                Result.failure()
            }
        }
    }
    private fun logError(message: String) {
        Log.e("MyWorker", message)
    }
    private fun showNotification(title: String, message: String) {
        val channelID = "GUESSIT"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (notificationManager.getNotificationChannel(channelID) == null) {
            val channel = NotificationChannel(
                channelID,
                "Channel Daily",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.NotMessage)
            }

            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent = NavDeepLinkBuilder(context)
            .setComponentName(LoginActivity::class.java)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.dailyFragment)
            .createPendingIntent()

        val builder = NotificationCompat.Builder(context, channelID)
            .setContentIntent(pendingIntent)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(123, builder.build())
        Log.d("Notification", "Notification should be shown")
    }
    fun saveUser(user: User) {
        FirebaseFirestore.getInstance().collection("users").document(user.email).set(
            hashMapOf(
                UserManager.FRIENDS to user.friends,
                UserManager.PROVIDER to user.provider,
                UserManager.EMAIL to user.email,
                UserManager.NAME to user.name,
                UserManager.POINT to user.point,
                UserManager.LEVEL to user.level,
                UserManager.COMPLETE_LEVEL to user.completeLevel,
                UserManager.COUNTRY_ENABLE to user.countryEnable,
                UserManager.SERIE_ENABLE to user.serieEnable,
                UserManager.FOOTBALL_ENABLE to user.footballEnable,
                UserManager.STAT_COUNTRY to user.statCountry,
                UserManager.STAT_SERIE to user.statSerie,
                UserManager.STAT_FOOTBALL to user.statFootball
            )
        )
    }
    suspend fun loadUser(email: String): Resource {
        try {
            val document = FirebaseFirestore.getInstance().collection("users").document(email).get().await()
            val user = User(
                document.get(UserManager.EMAIL) as String,
                document.get(UserManager.NAME) as String,
                document.get(UserManager.FRIENDS) as List<String>,
                (document.get(UserManager.POINT) as Number).toInt(),
                ProviderType.valueOf(document.get(UserManager.PROVIDER) as String),
                (document.get(UserManager.LEVEL) as Number).toInt(),
                "",
                (document.get(UserManager.COMPLETE_LEVEL) as Number).toInt(),
                (document.get(UserManager.COUNTRY_ENABLE) as Boolean),
                (document.get(UserManager.SERIE_ENABLE) as Boolean),
                (document.get(UserManager.FOOTBALL_ENABLE) as Boolean),
                (document.get(UserManager.STAT_COUNTRY) as Number).toInt(),
                (document.get(UserManager.STAT_SERIE) as Number).toInt(),
                (document.get(UserManager.STAT_FOOTBALL) as Number).toInt(),
            )
            return Resource.Success(user)
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }

}