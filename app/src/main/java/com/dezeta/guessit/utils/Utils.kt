package com.dezeta.guessit

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import com.dezeta.guessit.utils.Locator
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException


fun showSnackbar(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
}

fun loadImageFromInternalStorage(imageName: String): Bitmap? {
    return try {
        val myPath = File("${Locator.ImagePath}$imageName.jpg")
        BitmapFactory.decodeStream(FileInputStream(myPath))
    } catch (e: FileNotFoundException) {
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

