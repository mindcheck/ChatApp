package com.example.services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.chatapp.App
import com.example.chatapp.constants.*
import com.example.model.CreateUserModel
import org.json.JSONObject

object AuthService {

//    var userEmail=""
//    var token=""
//    var isLoggedIn = false


    fun  registerUser(pass:String,email:String,context: Context,complete:(Boolean)->Unit){

        val jsonObj = JSONObject()
        jsonObj.put("email",email)
        jsonObj.put("password",pass)

        val jsonBody = jsonObj.toString()

        val registerRequest = object : StringRequest(Method.POST,REGISTER,Response.Listener {
                response->
            print(response)
            complete(true)

        },Response.ErrorListener {
            error->
            Log.d("TAG","$error")
            complete(false)
        })
        {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return jsonBody.toByteArray()
            }
        }

        App.pref.requestQueue.add(registerRequest)

    }


    fun  loginUser(pass:String,email:String,context: Context,complete:(Boolean)->Unit){

        val jsonObj = JSONObject()
        jsonObj.put("email",email)
        jsonObj.put("password",pass)

        val jsonBody = jsonObj.toString()

        val loginRequest = object : JsonObjectRequest(Method.POST, LOGIN,null,Response.Listener {
                response->
            print(response)

            App.pref.userEmail =response.getString("user")
            App.pref.authToken=response.getString("token")
            App.pref.isLoggedIn = true

            complete(true)

        },Response.ErrorListener {
                error->
            Log.d("TAG","$error")
            complete(false)
        })
        {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return jsonBody.toByteArray()
            }
        }

        App.pref.requestQueue.add(loginRequest)
    }

    fun  createUser(name:String,email:String,avatarColor:String,avatarName:String,context: Context,complete:(Boolean)->Unit){

        val jsonObj = JSONObject()

        try {
            jsonObj.put("email",email)
            jsonObj.put("name",name)
            jsonObj.put("avatarName",avatarName)
            jsonObj.put("avatarColor",avatarColor)
        }catch (e :Exception){

        }


        val jsonBody = jsonObj.toString()

        val createReqest = object : JsonObjectRequest(Method.POST, CREATE_USER,null,Response.Listener {
                response->
            print(response)
            CreateUserModel.avatarColor=response.getString("avatarColor")
            CreateUserModel.avatarName=response.getString("avatarName")
            CreateUserModel.email=response.getString("email")
            CreateUserModel.name=response.getString("name")
            CreateUserModel._id=response.getString("_id")

            complete(true)

        },Response.ErrorListener {
                error->
            Log.d("TAG","$error")
            complete(false)
        })
        {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return jsonBody.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val mapHeader = HashMap<String,String>()
                mapHeader.put("Authorization","Bearer ${App.pref.authToken}")
                return mapHeader
            }
        }

        App.pref.requestQueue.add(createReqest)
    }


    fun  getUserByEmail(context: Context,complete:(Boolean)->Unit){

        val getUserByEmail = object : JsonObjectRequest(Method.GET, "${GET_USER_BY_EMAIL}/${App.pref.userEmail}",null,Response.Listener {
                response->
            print(response)
            CreateUserModel.avatarColor=response.getString("avatarColor")
            CreateUserModel.avatarName=response.getString("avatarName")
            CreateUserModel.email=response.getString("email")
            CreateUserModel.name=response.getString("name")
            CreateUserModel._id=response.getString("_id")

            val userEmailBrd = Intent(BROADCARD_MESSAGE_INTENT)

            LocalBroadcastManager.getInstance(context).sendBroadcast(userEmailBrd)

            complete(true)

        },Response.ErrorListener {
                error->
            Log.d("TAG","$error")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val mapHeader = HashMap<String,String>()
                mapHeader.put("Authorization","Bearer ${App.pref.authToken}")
                return mapHeader
            }
        }

        App.pref.requestQueue.add(getUserByEmail)
    }



}