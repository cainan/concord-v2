package com.alura.concord.network

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream

object DownloadService {

    suspend fun makeDownloadByURL(
        url: String,
        onDownloadFinished: (InputStream) -> Unit,
        onFailureDownload: () -> Unit,
    ) {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        try {
            withContext(IO) {
                client.newCall(request).execute().let { response ->
                    response.body?.byteStream()?.let { fileData: InputStream ->
                        withContext(Main) {
                            onDownloadFinished(fileData)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            onFailureDownload()
        }
    }
}