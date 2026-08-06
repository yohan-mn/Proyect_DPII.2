package com.proyect.travelhub.data.model

data class ChatConversation(
    val chatId: String = "",
    val otherUserId: String = "",
    val otherUserName: String = "",
    val otherUserPhoto: String = "",
    val lastMessage: String = "",
    val lastTimestamp: Long = System.currentTimeMillis()
)
