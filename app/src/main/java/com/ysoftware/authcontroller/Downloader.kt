package com.ysoftware.authcontroller

import android.os.AsyncTask
import android.util.Log
import java.io.BufferedInputStream
import java.net.URL

internal class download(val url:String, val handler: (String?) -> Unit) : AsyncTask<String, Void, Void>() {

    init {
        execute()
    }

    override fun doInBackground(vararg args: String): Void? {
        try {
            val url = URL(args[0])
            val connection = url.openConnection()
            connection.connect()

            val input = BufferedInputStream(url.openStream(), 8192)

            var bytesRead = 0
            val contents = ByteArray(1024)
            var output = ""
            while (bytesRead != -1) {
                bytesRead = input.read(contents)
                output += String(contents, 0, bytesRead)
            }
            handler(output)
        }
        catch (e: Exception) {
            Log.e("Error: ", e.message)
            handler(null)
        }

        return null
    }
}