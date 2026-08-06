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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.proyect.travelhub.data.model.ChatConversation
import java.text.SimpleDateFormat
import java.util.*

// ---------------------------------------------------------------------------
// Misma paleta usada en Login/RegisterScreen/CatalogScreen (inspirada en el
// Lago Titicaca) para mantener consistencia visual entre pantallas.
// No afecta la l�gica.
// ---------------------------------------------------------------------------
private val TiticacaDeepBlue = Color(0xFF0D3B66)
private val TiticacaBlue = Color(0xFF1976D2)
private val TiticacaTurquoise = Color(0xFF14B8A6)
private val TiticacaSky = Color(0xFFEAF4FB)
private val TiticacaGold = Color(0xFFF2A93B)
private val SurfaceSoft = Color(0xFFFFFFFF)
private val TextMuted = Color(0xFF5B6B79)

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
        containerColor = TiticacaSky,
        topBar = {
            TopAppBar(
                title = { Text("Mis Mensajes / Chat", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TiticacaDeepBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
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
                        tint = TiticacaTurquoise.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "A�n no tienes conversaciones activas.",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TiticacaDeepBlue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Contacta a un prestador desde el cat�logo para chatear.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(conversations) { item ->
                    ConversationItemRow(
                        item = item,
                        onClick = {
                            onConversationClick(item.chatId, item.otherUserId, item.otherUserName)
                        }
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
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(16.dp), clip = false)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = SurfaceSoft
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = TiticacaTurquoise,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = item.otherUserName.take(1).uppercase().ifBlank { "U" },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.otherUserName.ifBlank { "Usuario TravelHub" },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TiticacaDeepBlue
                    )
                    if (item.lastTimestamp > 0) {
                        val date = Date(item.lastTimestamp)
                        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
                        Text(
                            text = format.format(date),
                            style = MaterialTheme.typography.labelSmall,
                            color = TiticacaGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.lastMessage.ifBlank { "Sin mensajes a�n" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    maxLines = 1
                )
            }
        }
    }
}