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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.proyect.travelhub.data.model.ServiceCategory
import com.proyect.travelhub.data.model.ServiceItem

// ---------------------------------------------------------------------------
// Misma paleta usada en Login/RegisterScreen (inspirada en el Lago Titicaca)
// para mantener consistencia visual entre pantallas. No afecta la lógica.
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

    val chipColors = FilterChipDefaults.filterChipColors(
        containerColor = SurfaceSoft,
        selectedContainerColor = TiticacaTurquoise.copy(alpha = 0.15f),
        selectedLabelColor = TiticacaDeepBlue,
        labelColor = TextMuted
    )

    val chipBorder = FilterChipDefaults.filterChipBorder(
        enabled = true,
        selected = false,
        borderColor = TiticacaBlue.copy(alpha = 0.2f),
        selectedBorderColor = TiticacaTurquoise,
        selectedBorderWidth = 1.5.dp
    )

    Scaffold(
        containerColor = TiticacaSky,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Catálogo de Servicios Turísticos",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TiticacaDeepBlue,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
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
            NavigationBar(
                containerColor = SurfaceSoft,
                contentColor = TiticacaDeepBlue
            ) {
                val navColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TiticacaTurquoise,
                    selectedTextColor = TiticacaTurquoise,
                    indicatorColor = TiticacaTurquoise.copy(alpha = 0.12f),
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    label = { Text("Catálogo") },
                    icon = { Icon(Icons.Default.Storefront, contentDescription = null) },
                    colors = navColors
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToItinerary,
                    label = { Text("Itinerario") },
                    icon = { Icon(Icons.Default.Map, contentDescription = null) },
                    colors = navColors
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToCalculator,
                    label = { Text("Calculadora") },
                    icon = { Icon(Icons.Default.Calculate, contentDescription = null) },
                    colors = navColors
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToChatList,
                    label = { Text("Reservas / Chat") },
                    icon = { Icon(Icons.Default.Chat, contentDescription = null) },
                    colors = navColors
                )
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
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { viewModel.loadServices(null) },
                        label = { Text("✨ Todos", fontWeight = FontWeight.SemiBold) },
                        shape = RoundedCornerShape(14.dp),
                        colors = chipColors,
                        border = chipBorder
                    )
                }
                items(ServiceCategory.values()) { category ->
                    val label = when (category) {
                        ServiceCategory.HOSPEDAJE -> "🏨 Hospedaje"
                        ServiceCategory.ALIMENTACION -> "🍽️ Restaurantes"
                        ServiceCategory.GUIA -> "🧭 Guías Turísticos"
                        ServiceCategory.TRANSPORTE -> "🚌 Transporte"
                        ServiceCategory.TRADUCCION -> "🗣️ Traducción"
                    }
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { viewModel.loadServices(category) },
                        label = { Text(label, fontWeight = FontWeight.SemiBold) },
                        shape = RoundedCornerShape(14.dp),
                        colors = chipColors,
                        border = chipBorder
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TiticacaTurquoise)
                }
            } else if (services.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = TextMuted.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "No hay servicios disponibles en esta categoría.",
                            color = TextMuted
                        )
                    }
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
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(20.dp), clip = false)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            // Imagen del servicio
            AsyncImage(
                model = photoUrl,
                contentDescription = service.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = service.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TiticacaDeepBlue,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    Badge(containerColor = TiticacaTurquoise.copy(alpha = 0.15f)) {
                        Text(
                            text = service.category.name,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = TiticacaDeepBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = service.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = TiticacaGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${service.rating} • ${service.location}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                    Text(
                        text = "S/ ${service.pricePerDayOrUnit}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TiticacaBlue
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onChatClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TiticacaBlue.copy(alpha = 0.35f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TiticacaDeepBlue)
                ) {
                    Icon(
                        Icons.Default.Chat,
                        contentDescription = null,
                        tint = TiticacaTurquoise,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Chatear / Reservar con Prestador", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}