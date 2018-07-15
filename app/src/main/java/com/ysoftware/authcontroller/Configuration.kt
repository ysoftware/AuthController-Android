package com.ysoftware.authcontroller

class Configuration {

    var default = Configuration()

    var shouldUpdateOnlineStatus = true

    var shouldUpdateLocation = true

    var requiresAuthentication = true

    var locationUpdateInterval = 120

    var onlineStatusUpdateInterval = 60
}