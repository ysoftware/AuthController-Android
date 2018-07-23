package com.ysoftware.authcontroller

import android.content.Context

internal class Blocker(var context: Context) {

    fun checkBlocked(completion: (Boolean) -> Unit) {
        val bundleId = context.getPackageName()
        val url = "https://ysoftware.ru/AuthController/$bundleId.json"
        download(url) {
            completion(it?.contains("true") ?: false)
        }
    }
}