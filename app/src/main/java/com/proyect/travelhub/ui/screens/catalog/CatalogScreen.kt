package com.proyect.travelhub.ui.screens.catalog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.proyect.travelhub.data.model.ServiceCategory
import com.proyect.travelhub.data.model.ServiceItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    onServiceClick: (String) -> Unit,
    onNavigateToItinerary: () -> Unit,
    onNavigateToCalculator: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToChat: (chatId: String, otherUserId: String, name: String) -> Unit,
    onNavigateToChatList: () -> Unit,
    viewModel: CatalogViewModel = viewModel()
) {
    val services by viewModel.services.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo de Servicios Turísticos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    IconButton(onClick = onNavigateToChatList) {
                        Icon(Icons.Default.Chat, contentDescription = "Mis Chats")
                    }
                    IconButton(onClick = { viewModel.loadServices(selectedCategory) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Mi Perfil")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(selected = true, onClick = { }, label = { Text("Catálogo") }, icon = { Icon(Icons.Default.Storefront, contentDescription = null) })
                NavigationBarItem(selected = false, onClick = onNavigateToItinerary, label = { Text("Itinerario") }, icon = { Icon(Icons.Default.Map, contentDescription = null) })
                NavigationBarItem(selected = false, onClick = onNavigateToCalculator, label = { Text("Calculadora") }, icon = { Icon(Icons.Default.Calculate, contentDescription = null) })
                NavigationBarItem(selected = false, onClick = onNavigateToChatList, label = { Text("Reservas / Chat") }, icon = { Icon(Icons.Default.Chat, contentDescription = null) })
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filtros de Categorías
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { viewModel.loadServices(null) },
                        label = { Text("✨ Todos") }
                    )
                }
                items(ServiceCategory.values()) { category ->
                    val label = when (category) {
                        ServiceCategory.HOSPEDAJE -> "🏨 Hospedaje"
                        ServiceCategory.ALIMENTACION -> "🍽️ Restaurantes"
                        ServiceCategory.GUIA -> "🗺️ Guías Turísticos"
                        ServiceCategory.TRANSPORTE -> "🚌 Transporte"
                        ServiceCategory.TRADUCCION -> "🗣️ Traducción"
                    }
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { viewModel.loadServices(category) },
                        label = { Text(label) }
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (services.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay servicios disponibles en esta categoría.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(services) { service ->
                        ServiceCard(
                            service = service,
                            onClick = { onServiceClick(service.id) },
                            onChatClick = {
                                val providerId = service.providerId.ifBlank { "provider_1" }
                                val chatId = "chat_${providerId}"
                                onNavigateToChat(chatId, providerId, service.title)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceCard(service: ServiceItem, onClick: () -> Unit, onChatClick: () -> Unit) {
    val photoUrl = if (service.imageUrls.isNotEmpty() && service.imageUrls.first().isNotBlank()) {
        service.imageUrls.first()
    } else {
        when (service.category) {
            ServiceCategory.HOSPEDAJE -> "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800"
            ServiceCategory.ALIMENTACION -> "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800"
            ServiceCategory.GUIA -> "https://images.unsplash.com/photo-1526392060635-9d6019884377?w=800"
            ServiceCategory.TRANSPORTE -> "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=800"
            ServiceCategory.TRADUCCION -> "https://images.unsplash.com/photo-1455390582262-044cdead277a?w=800"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Imagen del servicio
            AsyncImage(
                model = photoUrl,
                contentDescription = service.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = service.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                        Text(
                            text = service.category.name,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = service.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${service.rating} · ${service.location}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Text(
                        text = "S/ ${service.pricePerDayOrUnit}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onChatClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Chatear / Reservar con Prestador")
                }
            }
        }
    }
}