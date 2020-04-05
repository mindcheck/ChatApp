package com.example.model

class ChannelModel(val channelName: String, val channelDesc: String,val channelId: String){

    override fun toString(): String {
        return "#$channelName"
    }

}