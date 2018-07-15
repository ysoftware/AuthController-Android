package com.ysoftware.authcontroller

import android.location.Location

open class AuthNetworking<U:AuthControllerUser> {

    open fun getUserId(): String? {
        throw Exception("override AuthNetworking.getUserId()")
    }

    open fun observeUser(id:String, block: (U?)->Unit): UserObserver {
        throw Exception("")
    }

    open fun onAuthStateChanged(block: ()->Unit) {
        throw Exception("")
    }

    open fun signOut() {
        throw Exception("")
    }

    open fun updateLocation(location: Location) { }

    open fun updateVersionCode() {}

    open fun updateLastSeen() {}

    open fun updateToken() {}

    open fun removeToken() {}
}

interface UserObserver {

    fun remove()
}