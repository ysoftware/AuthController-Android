package com.ysoftware.authcontroller

import android.location.Location

interface AuthLocation {

    fun requestLocation(block: (Location?)->Unit)
}