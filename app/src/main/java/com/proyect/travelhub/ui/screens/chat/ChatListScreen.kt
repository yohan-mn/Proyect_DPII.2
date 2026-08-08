package com.proyect.travelhub.ui.screens.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.proyect.travelhub.data.model.ChatConversation
import java.text.SimpleDateFormat
import java.util.*

// ---------------------------------------------------------------------------
// Paleta inspirada en las referencias (salón/barbería Casca)
// ---------------------------------------------------------------------------
private val PrimaryOrange      = Color(0xFFF5A623)
private val PrimaryOrangeLight = Color(0xFFFFF3E0)
private val Background         = Color(0xFFE3F2FD)
private val SurfaceSoft        = Color(0xFFFFFFFF)
private val TextPrimary        = Color(0xFF1F1F1F)
private val TextSecondary      = Color(0xFF9E9E9E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    onConversationClick: (chatId: String, otherUserId: String, otherUserName: String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = viewModel()
) {
    val conversations by viewModel.conversations.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadConversations()
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Mis Mensajes / Chat", fontWeight = FontWeight.Bold) },
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
        if (conversations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ChatBubble,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = PrimaryOrange.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "A?n no tienes conversaciones activas.",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Contacta a un prestador desde el cat?logo para chatear.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                items(conversations) { item ->
                    ConversationItemRow(
                        item = item,
                        onClick = {
                            onConversationClick(item.chatId, item.otherUserId, item.otherUserName)
                        }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 88.dp, end = 16.dp),
                        color = TextSecondary.copy(alpha = 0.1f),
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

@Composable
fun ConversationItemRow(
    item: ChatConversation,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar circular naranja (estilo referencia)
        Surface(
            shape = CircleShape,
            color = PrimaryOrange,
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = item.otherUserName.take(1).uppercase().ifBlank { "U" },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.otherUserName.ifBlank { "Usuario TravelHub" },
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.lastMessage.ifBlank { "Sin mensajes a?n" },
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                maxLines = 1
            )
        }

        if (item.lastTimestamp > 0) {
            val date = Date(item.lastTimestamp)
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            Text(
                text = format.format(date),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}