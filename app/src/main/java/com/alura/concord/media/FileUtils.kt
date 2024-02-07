package com.alura.concord.media

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
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
    onFailure: () -> Unit,
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
        } else {
            onFailure()
        }
    }
}

fun Context.openWith(mediaToOpen: String) {
    val file = File(mediaToOpen)

    val fileMimeType = file.getMimeType()

    val contentUri: Uri =
        getFileUriProvider(mediaToOpen)

    val shareIntent = Intent().apply {
        action = Intent.ACTION_VIEW
        setDataAndType(contentUri, fileMimeType)
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
    startActivity(Intent.createChooser(shareIntent, "Open with"))
}

private fun Context.getFileUriProvider(mediaToOpen: String): Uri {
    return FileProvider.getUriForFile(this, "com.alura.concord.fileprovider", File(mediaToOpen))
}

private fun File.getMimeType(): String {
    val fileExtension = MimeTypeMap.getFileExtensionFromUrl(Uri.encode(path))
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension) ?: "*/*"
}

fun Context.shareFile(mediaToOpen: String) {
    val file = File(mediaToOpen)

    val fileMimeType = file.getMimeType()

    val contentUri: Uri =
        getFileUriProvider(mediaToOpen)

    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, contentUri)
        type = fileMimeType
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
    startActivity(Intent.createChooser(shareIntent, "Share with"))
}

fun Context.saveOnExternalStorage(mediaLink: String, destinationUri: Uri) {
    val sourceFile = File(mediaLink)

    contentResolver.openOutputStream(destinationUri)?.use { outputStream ->
        sourceFile.inputStream().use { inputStream ->
            inputStream.copyTo(outputStream)
        }
    }

}