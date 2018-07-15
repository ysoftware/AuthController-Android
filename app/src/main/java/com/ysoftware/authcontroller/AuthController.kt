package com.ysoftware.authcontroller

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import java.util.*

class AuthController<U : AuthControllerUser> {

    lateinit private var context: Context

    private var configuration = Configuration()

    private var locationService: AuthLocation? = null
    private var analyticsService: AuthAnalytics<U>? = null
    lateinit private var loginPresenter: AuthLogin
    lateinit private var editProfilePresenter: AuthEditProfile
    lateinit private var networkService: AuthNetworking<U>
    lateinit private var settingsService: AuthSettings

    private var onlineStatusTimer: Timer? = null
    private var locationTimer: Timer? = null

    private var handle: UserObserver? = null

    var user: U? = null
        private set

    val userId: String?
        get() = networkService.getUserId()

    // Methods

    fun configure(context: Context, configuration: Configuration = Configuration(), networkService: AuthNetworking<U>,
                  loginPresenter: AuthLogin, editProfilePresenter: AuthEditProfile,
                  locationService: AuthLocation? = null, analyticsService: AuthAnalytics<U>? = null,
                  settingsService: AuthSettings = DefaultSettingsService()) {

        this.context = context
        this.configuration = configuration
        this.loginPresenter = loginPresenter
        this.editProfilePresenter = editProfilePresenter
        this.locationService = locationService
        this.networkService = networkService
        this.analyticsService = analyticsService
        this.settingsService = settingsService
        this.setup()
        this.checkLogin()
    }

    fun signOut() {
        stopObserving()
        if (user != null) {
            networkService.removeToken()
        }
        networkService.signOut()
        if (configuration.requiresAuthentication) {
            showLogin()
        }
        postNotification(authControllerDidSignOut)
        settingsService.clear()
    }

    val isLoggedIn: Boolean
        get() = user != null

    fun showLogin() {
        loginPresenter.showLogin()
        postNotification(authControllerDidShowLogin)
    }

    fun hideLogin() {
        loginPresenter.hideLogin()
        postNotification(authControllerDidHideLogin)
    }

    fun checkLogin(): Boolean {
        if (networkService.getUserId() == null) {
            signOut()
            return false
        }
        else {
            startObserving()
            return true
        }
    }

    // Private

    fun setup() {
        startObserving()
        networkService.onAuthStateChanged {
            checkLogin()
        }
    }

    fun setupTimers() {
        if (configuration.shouldUpdateOnlineStatus) {
            // todo
        }

        if (configuration.shouldUpdateLocation) {
            // todo
        }
    }

    fun updateUser(newValue:U?) {
        if (newValue == null) {
            signOut()
            return
        }

        if (user != null) { // data update
            user = newValue
            postNotification(authControllerDidUpdateUserData)
        }
        else { // just logged in
            user = newValue

            hideLogin()
            setupTrackingFor(user)
            setupTimers()
            networkService.updateToken()
            networkService.updateVersionCode()
            postNotification(authControllerDidSignIn)
        }

        if (!newValue.isProfileComplete) {
            editProfilePresenter.present()
        }
    }

    fun startObserving() {
        val currentUserId = networkService.getUserId()
        if (currentUserId == null) {
            signOut()
            return
        }

        if (handle == null) {
            handle = networkService.observeUser(currentUserId) { updateUser(it) }
        }
    }

    fun stopObserving() {
        handle?.remove()
        handle = null
        user = null

        onlineStatusTimer?.cancel()
        onlineStatusTimer?.purge()

        locationTimer?.cancel()
        locationTimer?.purge()
    }

    fun setupTrackingFor(user:U?) {
        analyticsService?.setUser(user)
    }

    // Таймеры

    fun updateUserOnline() {
        if (configuration.shouldUpdateOnlineStatus) {
            networkService.updateLastSeen()
        }
    }

    fun updateLocation() {
        if (configuration.shouldUpdateLocation && settingsService.shouldAccessLocation) {
            locationService?.requestLocation { location ->
                if (location != null) {
                    networkService.updateLocation(location)
                    postNotification(authControllerDidUpdateLocation)
                }
            }
        }
    }

    fun postNotification(notification:String) {
        val intent = Intent(notification)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }
}