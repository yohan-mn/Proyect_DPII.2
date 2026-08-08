package com.proyect.travelhub.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.proyect.travelhub.data.model.ChatMessage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.proyect.travelhub.data.repository.AuthRepository

class ChatRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun getRealtimeMessages(chatId: String): Flow<List<ChatMessage>> = callbackFlow {
        val messagesRef = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)

        val listener = messagesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val messages = snapshot.toObjects(ChatMessage::class.java)
                trySend(messages)
            }
        }

        awaitClose { listener.remove() }
    }

    suspend fun sendMessage(chatId: String, message: ChatMessage): Result<Boolean> {
        return try {
            val docRef = firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .document()
            val newMsg = message.copy(id = docRef.id)
            docRef.set(newMsg).await()

            // Actualizar resumen en lista de chats para ambos usuarios
            // Obtener nombre real del receptor
            val authRepository = AuthRepository()
            val receiver = authRepository.getUserById(message.receiverId)
            val receiverName = receiver?.name ?: "Usuario"

// Resumen que verá el emisor
            val senderSummary = mapOf(
                "chatId" to chatId,
                "otherUserId" to message.receiverId,
                "otherUserName" to receiverName,
                "otherUserPhoto" to "",
                "lastMessage" to message.messageText,
                "lastTimestamp" to message.timestamp
            )

// Resumen que verá el receptor
            val receiverSummary = mapOf(
                "chatId" to chatId,
                "otherUserId" to message.senderId,
                "otherUserName" to message.senderName,
                "otherUserPhoto" to "",
                "lastMessage" to message.messageText,
                "lastTimestamp" to message.timestamp
            )

            if (message.senderId.isNotBlank()) {
                firestore.collection("users").document(message.senderId)
                    .collection("conversations").document(chatId)
                    .set(senderSummary, com.google.firebase.firestore.SetOptions.merge())            }
            if (message.receiverId.isNotBlank()) {
                firestore.collection("users").document(message.receiverId)
                    .collection("conversations").document(chatId)
                    .set(receiverSummary, com.google.firebase.firestore.SetOptions.merge())            }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getUserConversations(userId: String): Flow<List<com.proyect.travelhub.data.model.ChatConversation>> = callbackFlow {
        if (userId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val ref = firestore.collection("users")
            .document(userId)
            .collection("conversations")
            .orderBy("lastTimestamp", Query.Direction.DESCENDING)

        val listener = ref.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val list = snapshot.documents.map { doc ->
                com.proyect.travelhub.data.model.ChatConversation(
                    chatId = doc.getString("chatId") ?: doc.id,
                    otherUserId = doc.getString("otherUserId") ?: "",
                    otherUserName = doc.getString("otherUserName") ?: "Usuario",
                    otherUserPhoto = doc.getString("otherUserPhoto") ?: "",
                    lastMessage = doc.getString("lastMessage") ?: "",
                    lastTimestamp = doc.getLong("lastTimestamp") ?: 0L
                )
            }
            trySend(list)
        }

        awaitClose { listener.remove() }
    }
}