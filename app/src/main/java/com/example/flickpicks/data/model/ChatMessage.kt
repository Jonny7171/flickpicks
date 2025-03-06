package com.example.flickpicks.data.model

data class ChatMessage(
    val sender: String,
    val message: String,
    val isMe: Boolean
)
