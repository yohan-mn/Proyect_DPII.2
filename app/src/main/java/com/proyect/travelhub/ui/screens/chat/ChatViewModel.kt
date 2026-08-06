package com.proyect.travelhub.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyect.travelhub.data.model.ChatMessage
import com.proyect.travelhub.data.repository.AuthRepository
import com.proyect.travelhub.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatRepository: ChatRepository = ChatRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    val currentUserId: String
        get() = authRepository.currentUserId ?: ""

    private val _conversations = MutableStateFlow<List<com.proyect.travelhub.data.model.ChatConversation>>(emptyList())
    val conversations: StateFlow<List<com.proyect.travelhub.data.model.ChatConversation>> = _conversations

    var currentUserName: String = "Usuario"
        private set

    init {
        loadUserProfile()
        loadConversations()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUserProfile()
            if (user != null) {
                currentUserName = user.name
            }
        }
    }

    fun loadConversations() {
        if (currentUserId.isBlank()) return
        viewModelScope.launch {
            chatRepository.getUserConversations(currentUserId).collect { list ->
                _conversations.value = list
            }
        }
    }

    fun listenToChat(chatId: String) {
        viewModelScope.launch {
            chatRepository.getRealtimeMessages(chatId).collect { list ->
                _messages.value = list
            }
        }
    }

    fun sendMessage(chatId: String, receiverId: String, text: String) {
        if (text.isBlank()) return
        val msg = ChatMessage(
            chatId = chatId,
            senderId = currentUserId,
            senderName = currentUserName,
            receiverId = receiverId,
            messageText = text
        )
        viewModelScope.launch {
            chatRepository.sendMessage(chatId, msg)
        }
    }
}