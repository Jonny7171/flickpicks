package com.example.flickpicks.data.model

import com.google.firebase.firestore.PropertyName

data class ChatMessage(
    @get:PropertyName("sender") @set:PropertyName("sender")
    var sender: String = "",

    @get:PropertyName("message") @set:PropertyName("message")
    var message: String = "",

    @get:PropertyName("isMe") @set:PropertyName("isMe")
    var isMe: Boolean = false,

    @get:PropertyName("timestamp") @set:PropertyName("timestamp")
    var timestamp: Long = System.currentTimeMillis()
){

    constructor() : this("", "", false, 0L)
}
