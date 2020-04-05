package com.example.model

import android.graphics.Color
import com.example.chatapp.App
import com.example.services.AuthService
import java.util.*

object CreateUserModel{

    var avatarColor=""
    var avatarName=""
    var email=""
    var name=""
    var _id=""


    fun logout() {
        _id = ""
        avatarColor = ""
        avatarName = ""
        email = ""
        name = ""
        App.pref.authToken = ""
        App.pref.userEmail = ""
        App.pref.isLoggedIn = false
        MessageService.clearMessages()
        MessageService.clearChannels()
    }

    fun returnAvatarColor(components: String) : Int {
        val strippedColor = components
            .replace("[", "")
            .replace("]", "")
            .replace(",", "")

        var r = 0
        var g = 0
        var b = 0

        val scanner = Scanner(strippedColor)
        if (scanner.hasNext()) {
            r = (scanner.nextDouble() * 255).toInt()
            g = (scanner.nextDouble() * 255).toInt()
            b = (scanner.nextDouble() * 255).toInt()
        }

        return Color.rgb(r,g,b)
    }
}
