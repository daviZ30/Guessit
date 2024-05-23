package com.dezeta.guessit.utils

import android.content.Context
import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

class CloudStorageManager() {
    private val storage = Firebase.storage
    private val storageRef = storage.reference
    private val userid = FirebaseAuth.getInstance().currentUser?.uid
    fun getStorageReference(): StorageReference {
        return storageRef.child("images")
    }

    suspend fun updateFile(fileName: String, filePath: Uri) {
        val fileRef = getStorageReference().child(fileName)
        val uploadTask = fileRef.putFile(filePath)
        uploadTask.await()
    }

    suspend fun getUserImages(email: String): String {
        val listResult: ListResult = getStorageReference().listAll().await()
        for (item in listResult.items) {
            if (item.name == email) {
                val url = item.downloadUrl.await().toString()
                return url
            }
        }
        return "https://firebasestorage.googleapis.com/v0/b/guessit-dezeta.appspot.com/o/images%2Fprofile_u.png?alt=media&token=fa64ae06-cb43-4814-ae1c-cb8eabf0bbaf"
    }
}