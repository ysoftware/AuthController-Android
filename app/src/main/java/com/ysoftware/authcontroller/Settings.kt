package com.ysoftware.authcontroller

interface AuthSettings {

    fun clear()

    val shouldAccessLocation:Boolean
}

class DefaultSettingsService: AuthSettings {

    override fun clear() {

    }

    override val shouldAccessLocation: Boolean
        get() = false
}