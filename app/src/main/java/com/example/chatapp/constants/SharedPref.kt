package com.example.chatapp.constants

import android.content.Context
import android.content.SharedPreferences
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class SharedPref (context: Context){

    val PREFS_FILENAME = "prefs"

    val IS_LOGGED_IN = "isLoggedIn"
    val AUTH_TOKEN:String = "authToken"
    val USER_EMAIL:String = "userEmail"

    val prefs:SharedPreferences = context.getSharedPreferences(PREFS_FILENAME,0)

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(IS_LOGGED_IN, value).apply()

    var authToken: String?
        get() = prefs.getString(AUTH_TOKEN, "")
        set(value) = prefs.edit().putString(AUTH_TOKEN, value).apply()

    var userEmail: String?
        get() = prefs.getString(USER_EMAIL, "")
        set(value) = prefs.edit().putString(USER_EMAIL, value).apply()

    val requestQueue: RequestQueue = Volley.newRequestQueue(context)


}