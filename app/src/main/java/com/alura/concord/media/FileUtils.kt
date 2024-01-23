package com.alura.concord.media

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

fun Long.formatReadableFileSize(): String {
    val size = this
    val kilobyte = 1024
    val megaByte = kilobyte * 1024
    val gigaByte = megaByte * 1024

    return when {
        size < kilobyte -> "$size B"
        size < megaByte -> "${size / kilobyte} KB"
        size < gigaByte -> "${size / megaByte} MB"
        else -> "${size / gigaByte} GB"
    }
}

suspend fun Context.saveOnInternalStorage(
    inputStream: InputStream,
    fileName: String,
    onSuccess: (String) -> Unit,
) {
    val path = getExternalFilesDir("temp")
    Log.d("teste", "path: $path")
    val newFile = File(path, fileName)

    withContext(Dispatchers.IO) {
        newFile.outputStream().use { file ->
            inputStream.copyTo(file)
        }

        if (newFile.exists()) {
            onSuccess(newFile.path)
        }
    }
}