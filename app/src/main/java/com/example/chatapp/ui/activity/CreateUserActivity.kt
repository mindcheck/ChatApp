package com.example.chatapp.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.chatapp.R
import com.example.chatapp.constants.BROADCARD_MESSAGE_INTENT
import com.example.model.CreateUserModel
import com.example.services.AuthService
import kotlinx.android.synthetic.main.activity_create_user.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlin.random.Random

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profileDefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        createSpinner.visibility = View.INVISIBLE


    }

    fun generateUserAvatar(view: View){

        val randomNo = java.util.Random()

        val randomColor = randomNo.nextInt(2)

        val randomAvatar =  randomNo.nextInt(28)

        if(randomColor==0){
            userAvatar = "light$randomAvatar"
        }else{
            userAvatar="dark$randomAvatar"
        }


        val resourceId = resources.getIdentifier(userAvatar,"drawable",packageName)

        createAvatarImageView.setImageResource(resourceId)
    }

    fun generateColorClicked(view: View){
        val random = java.util.Random()
        val r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)

        createAvatarImageView.setBackgroundColor(Color.rgb(r,g,b))

        val savedR = r.toDouble() / 255
        val savedG = g.toDouble() / 255
        val savedB = b.toDouble() / 255

        avatarColor = "[$savedR, $savedG, $savedB, 1]"
    }

    fun createUserClicked(view: View){
        enableSpinner(true)
        val userName = createUserNameText.text.toString()
        val email = createEmailText.text.toString()
        val password = createPasswordText.text.toString()

        if (userName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            AuthService.registerUser(password, email, this) { complete ->
                if (complete) {
                    AuthService.loginUser(password,email,this){
                        loginSuccess->
                        if(loginSuccess){
                            AuthService.createUser(userName,email,avatarColor,userAvatar,this){
                                    createUserSuccess->
                                if(createUserSuccess){
                                    val brIntent = Intent(BROADCARD_MESSAGE_INTENT)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(brIntent)
                                    enableSpinner(false)
                                    finish()
                                }else{
                                    errorToast()
                                }
                            }
                        }else{
                            errorToast()
                        }
                    }
                }else{
                    errorToast()
                }
            }
        }
        else {
            Toast.makeText(this, "Make sure user name, email, and password are filled in.",
                Toast.LENGTH_LONG).show()
            enableSpinner(false)
        }
    }

    fun errorToast() {
        Toast.makeText(this, "Something went wrong, please try again.",
            Toast.LENGTH_LONG).show()
        enableSpinner(false)
    }

    fun enableSpinner(enable: Boolean) {
        if (enable) {
            createSpinner.visibility = View.VISIBLE
        } else {
            createSpinner.visibility = View.INVISIBLE
        }
        createUserBtn.isEnabled = !enable
        createAvatarImageView.isEnabled = !enable
        backgroundColorBtn.isEnabled = !enable
    }

}
