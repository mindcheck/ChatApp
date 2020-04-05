package com.example.chatapp

import android.app.Application
import android.content.SharedPreferences
import com.example.chatapp.constants.SharedPref

class App : Application() {

    companion object {
        lateinit var pref : SharedPref
    }


    override fun onCreate() {
        super.onCreate()
        pref = SharedPref(applicationContext)
    }



}