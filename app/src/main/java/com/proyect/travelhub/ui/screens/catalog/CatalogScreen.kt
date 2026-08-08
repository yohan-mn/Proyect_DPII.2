package com.proyect.travelhub.ui.screens.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.proyect.travelhub.data.model.ServiceCategory
import com.proyect.travelhub.data.model.ServiceItem

// ---------------------------------------------------------------------------
// Paleta inspirada en las referencias visuales (salón/barbería Casca)
// ---------------------------------------------------------------------------
private val PrimaryOrange      = Color(0xFFF5A623)
private val PrimaryOrangeLight = Color(0xFFFFF3E0)
private val Background         = Color(0xFFE3F2FD)
private val SurfaceSoft        = Color(0xFFFFFFFF)
private val InputBg            = Color(0xFFF5F5F5)
private val TextPrimary        = Color(0xFF1F1F1F)
private val TextSecondary      = Color(0xFF9E9E9E)
private val StarColor          = Color(0xFFFFB800)

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
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Catálogo ",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceSoft,
                    titleContentColor = TextPrimary,
                    actionIconContentColor = TextPrimary
                ),
                actions = {
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
                tonalElevation = 0.dp
            ) {
                val navColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryOrange,
                    selectedTextColor = PrimaryOrange,
                    indicatorColor = PrimaryOrange.copy(alpha = 0.12f),
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
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
            // Categorías estilo referencia: iconos circulares seleccionables
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(horizontal = 20.dp)
            ) {
                // Item "Todos"
                item {
                    CategoryCircleItem(
                        icon = Icons.Default.Dashboard,
                        label = "Todos",
                        isSelected = selectedCategory == null,
                        onClick = { viewModel.loadServices(null) }
                    )
                }
                items(ServiceCategory.values()) { category ->
                    val (icon, label) = when (category) {
                        ServiceCategory.HOSPEDAJE -> Icons.Default.Hotel to "Hospedaje"
                        ServiceCategory.ALIMENTACION -> Icons.Default.Restaurant to "Comida"
                        ServiceCategory.GUIA -> Icons.Default.Explore to "Guías"
                        ServiceCategory.TRANSPORTE -> Icons.Default.DirectionsCar to "Transporte"
                        ServiceCategory.TRADUCCION -> Icons.Default.Translate to "Traducción"
                    }
                    CategoryCircleItem(
                        icon = icon,
                        label = label,
                        isSelected = selectedCategory == category,
                        onClick = { viewModel.loadServices(category) }
                    )
                }
            }

            HorizontalDivider(color = TextSecondary.copy(alpha = 0.1f))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryOrange)
                }
            } else if (services.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = TextSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "No hay servicios disponibles en esta categoría.",
                            color = TextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(services) { service ->
                        ServiceCardRow(
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

// ---------------------------------------------------------------------------
// Categoría estilo referencia: círculo con icono + label debajo
// ---------------------------------------------------------------------------
@Composable
private fun CategoryCircleItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .shadow(
                    elevation = if (isSelected) 8.dp else 2.dp,
                    shape = CircleShape,
                    clip = false
                )
                .background(
                    color = if (isSelected) PrimaryOrange else InputBg,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color.White else TextSecondary,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (isSelected) PrimaryOrange else TextSecondary
        )
    }
}

// ---------------------------------------------------------------------------
// Tarjeta horizontal estilo referencia (Nearby Your Location)
// ---------------------------------------------------------------------------
@Composable
fun ServiceCardRow(service: ServiceItem, onClick: () -> Unit, onChatClick: () -> Unit) {
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
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp), clip = false)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen cuadrada redondeada (estilo referencia)
            AsyncImage(
                model = photoUrl,
                contentDescription = service.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = service.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = service.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = StarColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${service.rating}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${service.category.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "S/ ${service.pricePerDayOrUnit}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = PrimaryOrange
                )
            }

            // Botón de acción (chat) como icono circular naranja, estilo referencia
            FilledIconButton(
                onClick = onChatClick,
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = PrimaryOrangeLight,
                    contentColor = PrimaryOrange
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "Chatear",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}