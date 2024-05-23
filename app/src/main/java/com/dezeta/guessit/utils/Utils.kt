package com.dezeta.guessit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import com.dezeta.guessit.utils.Locator
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.SimpleTimeZone


fun showSnackbar(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
}


fun loadImageBitmapFromInternalStorage(imageName: String): Bitmap? {
    return try {
        val myPath = File("${Locator.ImagePath}$imageName.jpg")
        BitmapFactory.decodeStream(FileInputStream(myPath))
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        null
    }
}

fun loadImageUriFromInternalStorage(imageName: String): Uri? {
    return try {
        val myPath = File("${Locator.ImagePath}$imageName.jpg")
        if (myPath.exists()) {
            Uri.fromFile(myPath)
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
fun deleteImage(imageName: String) {
    val myPath = File("${Locator.ImagePath}$imageName.jpg")
    if (myPath.exists()) {
        if (myPath.delete()) {
            println("Imagen eliminada: $imageName.jpg")
        } else {
            println("No se pudo eliminar la imagen: $imageName.jpg")
        }
    } else {
        println("La imagen no existe: $imageName.jpg")
    }
}
fun saveToInternalStorage(bitmap: Bitmap, imageName: String) {
    val folder = File(Locator.ImagePath)
    if (!folder.exists()) {
        folder.mkdirs()
    }
    val myPath = File("${Locator.ImagePath}$imageName.jpg")
    try {
        val fos = FileOutputStream(myPath)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


