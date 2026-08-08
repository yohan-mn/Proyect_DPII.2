package com.proyect.travelhub.ui.screens.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.proyect.travelhub.data.model.ChatMessage
import androidx.compose.foundation.shape.CircleShape

// ---------------------------------------------------------------------------
// Paleta inspirada en las referencias (salón/barbería Casca)
// ---------------------------------------------------------------------------
private val PrimaryOrange      = Color(0xFFF5A623)
private val PrimaryOrangeLight = Color(0xFFFFF3E0)
private val Background         = Color(0xFFE3F2FD)
private val SurfaceSoft        = Color(0xFFFFFFFF)
private val InputBg            = Color(0xFFF5F5F5)
private val TextPrimary        = Color(0xFF1F1F1F)
private val TextSecondary      = Color(0xFF9E9E9E)
private val BubbleMe           = PrimaryOrange
private val BubbleOther        = Color(0xFFF0F0F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatId: String = "general_chat",
    receiverId: String = "provider_1",
    otherUserName: String = "Prestador de Servicio",
    onNavigateBack: () -> Unit = {},
    viewModel: ChatViewModel = viewModel()
) {
    val messages by viewModel.messages.collectAsState()
    var textInput by remember { mutableStateOf("") }

    LaunchedEffect(chatId) {
        viewModel.listenToChat(chatId)
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = PrimaryOrange,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = otherUserName.take(1).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                otherUserName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = TextPrimary
                            )
                            Text(
                                "En línea",
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryOrange
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceSoft,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = TextPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { msg ->
                    ChatBubble(msg = msg, isMe = msg.senderId == viewModel.currentUserId)
                }
            }

            Surface(
                color = SurfaceSoft,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = { Text("Escribe un mensaje...", color = TextSecondary) },
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.weight(1f),
                        maxLines = 3,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = InputBg,
                            unfocusedContainerColor = InputBg,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = PrimaryOrange,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    FloatingActionButton(
                        onClick = {
                            if (textInput.isNotBlank()) {
                                viewModel.sendMessage(chatId, receiverId, textInput)
                                textInput = ""
                            }
                        },
                        containerColor = PrimaryOrange,
                        contentColor = Color.White,
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Enviar")
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(msg: ChatMessage, isMe: Boolean) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isMe) 18.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 18.dp
            ),
            color = if (isMe) BubbleMe else BubbleOther,
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                if (!isMe && msg.senderName.isNotBlank()) {
                    Text(
                        text = msg.senderName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryOrange
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }
                Text(
                    text = msg.messageText,
                    color = if (isMe) Color.White else TextPrimary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}