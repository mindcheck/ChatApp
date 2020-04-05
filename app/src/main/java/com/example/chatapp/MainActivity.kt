package com.example.chatapp

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.MessageAdapter
import com.example.chatapp.constants.BROADCARD_MESSAGE_INTENT
import com.example.chatapp.constants.SOCKET_URL
import com.example.chatapp.ui.activity.LoginActivity
import com.example.model.ChannelModel
import com.example.model.CreateUserModel
import com.example.model.CreateUserModel.name
import com.example.model.Message
import com.example.model.MessageService
import com.example.services.AuthService
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter: ArrayAdapter<ChannelModel>

    var selectedChnnel : ChannelModel? = null

    lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        socket.connect()

        socket.on("channelCreated",emitterList)
        socket.on("messageCreated",emitterMessageList)

        LocalBroadcastManager.getInstance(this).registerReceiver(localBrodcast,
            IntentFilter(BROADCARD_MESSAGE_INTENT) )

        setupAdapters()

        if(App.pref.isLoggedIn){
            AuthService.getUserByEmail(this){

            }
        }


    }


    val localBrodcast = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {

            if(App.pref.isLoggedIn){
                userNameNavHeader.text = CreateUserModel.name
                userEmailNavHeader.text = CreateUserModel.email
                val resourceId = resources.getIdentifier(CreateUserModel.avatarName, "drawable",
                    packageName)
                userImageNavHeader.setImageResource(resourceId)
                userImageNavHeader.setBackgroundColor(CreateUserModel.returnAvatarColor(CreateUserModel.avatarColor))
                loginBtnNavHeader.text = "Logout"


                if (context != null) {
                    MessageService.getChannels(context) { complete ->
                        if (complete) {
                            if (MessageService.channel.count() > 0) {
                                selectedChnnel = MessageService.channel[0]
                                channelAdapter.notifyDataSetChanged()
                                updateWithChannel()
                            }
                        }
                    }
                }

            }

        }

    };

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }



    fun addChannelClicked(view: View) {
        if (App.pref.isLoggedIn) {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

            builder.setView(dialogView)
                .setPositiveButton("Add") { text , dailogInterface ->
                    val nameTextField = dialogView.findViewById<EditText>(R.id.addChannelNameTxt)
                    val descTextField = dialogView.findViewById<EditText>(R.id.addChannelDescTxt)
                    val channelName = nameTextField.text.toString()
                    val channelDesc = descTextField.text.toString()

                    socket.emit("newChannel",channelName,channelDesc)

                }
                .setNegativeButton("Cancel") { _, _ ->
                    // Cancel and close the dialog
                }
                .show()
        }
    }

    fun loginBtnNavClicked(view: View) {

        if(App.pref.isLoggedIn){
            CreateUserModel.logout()
            messageAdapter.notifyDataSetChanged()
            channelAdapter.notifyDataSetChanged()
            userNameNavHeader.text = ""
            userEmailNavHeader.text = ""
            userImageNavHeader.setImageResource(R.drawable.profiledefault)
            userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            loginBtnNavHeader.text = "Login"

        }
        else{
            val loginIntent = Intent(this,LoginActivity::class.java)
            startActivity(loginIntent)
        }


    }

    override fun onResume() {
        super.onResume()




    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localBrodcast)
        socket.disconnect()
        super.onDestroy()
    }


    private  val emitterList = Emitter.Listener {
            newChannel->

        if(App.pref.isLoggedIn) {

            runOnUiThread {
                val name = newChannel[0] as String
                val description = newChannel[1] as String
                val id = newChannel[2] as String
                val channelMode = ChannelModel(name, description, id)
                MessageService.channel.add(channelMode)
                channelAdapter.notifyDataSetChanged()
            }
        }
    }

    private  val emitterMessageList = Emitter.Listener {
            newMessage->

        runOnUiThread{
            if(App.pref.isLoggedIn){
                val channelId = newMessage[2] as String

                if(channelId == selectedChnnel?.channelId){

                    val message = newMessage[0] as String
                    val userName = newMessage[3] as String
                    val userAvatar = newMessage[4] as String
                    val userAvatarColor = newMessage[5] as String
                    val id = newMessage[6]  as String
                    val timeStamp = newMessage[7]  as String

                    val messageModel = Message(message,userName,channelId,userAvatar,userAvatarColor,id,timeStamp)

                    MessageService.messages.add(messageModel)

                    messageAdapter.notifyDataSetChanged()

                    messageListView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                }

            }


        }
    }

    private fun setupAdapters() {
        channelAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channel)
        channel_list.adapter = channelAdapter

        messageAdapter = MessageAdapter(this, MessageService.messages)
        messageListView.adapter = messageAdapter
        val layoutManager = LinearLayoutManager(this)
        messageListView.layoutManager = layoutManager



        channel_list.setOnItemClickListener() { _ , _, position: Int, _ ->
            selectedChnnel = MessageService.channel[position]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel()
        }


    }

    fun updateWithChannel() {
        mainChannelName.text = "#${selectedChnnel?.channelName}"
        if (selectedChnnel != null) {
            MessageService.getMessages(selectedChnnel!!.channelId) { complete ->
                if (complete) {
                    messageAdapter.notifyDataSetChanged()
                    if (messageAdapter.itemCount > 0) {
                        messageListView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                    }
                }
            }
        }
    }


    fun sendMsgBtnClicked(view: View) {

        if(App.pref.isLoggedIn
            &&  selectedChnnel!=null && !messageTextField.text.isEmpty()){
            socket.emit("newMessage",messageTextField.text.toString(),CreateUserModel._id,selectedChnnel!!.channelId,
                CreateUserModel.name,CreateUserModel.avatarName,CreateUserModel.avatarColor)
            messageTextField.text.clear()
            hideKeyboard()
        }


    }


    fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

}
