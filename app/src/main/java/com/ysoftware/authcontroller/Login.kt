package com.ysoftware.authcontroller

interface AuthLogin {

    fun showLogin()

    fun hideLogin()

    val isShowingLogin:Boolean
}


// MARK: - Default Implementation

class WindowLoginPresenter: AuthLogin {

    override val isShowingLogin: Boolean
        get() = false

    override fun showLogin() {

    }

    override fun hideLogin() {
        
    }
}