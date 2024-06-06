package com.dezeta.guessit.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dezeta.guessit.domain.Repository.UserManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MyWorkerFirebase(
    private val context: Context,
    private val params: WorkerParameters,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val email = params.inputData.getString("email")
        return withContext(Dispatchers.IO) {

            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.reload()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (!currentUser.isEmailVerified) {
                        UserManager.deleteUser(email!!)
                        println("EMAIL BORRADO")
                    }
                }
            }
            Result.success()
        }

    }
}