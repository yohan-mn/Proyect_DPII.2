package com.proyect.travelhub.ui.screens.itinerary

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.proyect.travelhub.data.model.ItineraryItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryScreen(
    onNavigateToCatalog: () -> Unit,
    onNavigateToCalculator: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToChatList: () -> Unit = {},
    viewModel: ItineraryViewModel = viewModel()
) {
    val items by viewModel.itineraryItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val routeResult by viewModel.routeResult.collectAsState()
    val userLocation by viewModel.currentUserLocation.collectAsState()
    val useCurrentLocStart by viewModel.useCurrentLocationAsStart.collectAsState()

    val punoCenter = LatLng(-15.8402, -70.0219)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(punoCenter, 11f)
    }

    val coroutineScope = rememberCoroutineScope()
    var showAddDialog by remember { mutableStateOf(false) }

    // Estado para la tarjeta de previsualización al presionar un marcador
    var selectedPreviewItem by remember { mutableStateOf<ItineraryItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Itinerario y Ruta") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.fetchCurrentLocation { loc ->
                                coroutineScope.launch {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(loc, 14f)
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Centrar en Mi Ubicación",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Mi Perfil")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(selected = false, onClick = onNavigateToCatalog, label = { Text("Catálogo") }, icon = { Icon(Icons.Default.Storefront, contentDescription = null) })
                NavigationBarItem(selected = true, onClick = { }, label = { Text("Itinerario") }, icon = { Icon(Icons.Default.Map, contentDescription = null) })
                NavigationBarItem(selected = false, onClick = onNavigateToCalculator, label = { Text("Calculadora") }, icon = { Icon(Icons.Default.Calculate, contentDescription = null) })
                NavigationBarItem(selected = false, onClick = onNavigateToChatList, label = { Text("Reservas / Chat") }, icon = { Icon(Icons.Default.Chat, contentDescription = null) })
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar lugar")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 🗺️ GOOGLE MAPS CON RUTA REAL Y FOTOS AL TOCAR MARCADORES
            Box(modifier = Modifier.fillMaxWidth().height(290.dp)) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        mapType = MapType.NORMAL,
                        isMyLocationEnabled = userLocation != null
                    )
                ) {
                    // Marcador de la ubicación del usuario si está disponible
                    userLocation?.let { loc ->
                        Marker(
                            state = MarkerState(position = loc),
                            title = "📍 Mi Ubicación Actual",
                            snippet = "Punto de inicio del recorrido"
                        )
                    }

                    // Marcadores en cada destino
                    items.forEach { item ->
                        Marker(
                            state = MarkerState(position = LatLng(item.latitude, item.longitude)),
                            title = "Día ${item.dayNumber}: ${item.title}",
                            snippet = "Toca para ver foto e información",
                            onClick = {
                                selectedPreviewItem = item
                                coroutineScope.launch {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(LatLng(item.latitude, item.longitude), 14f)
                                    )
                                }
                                true
                            }
                        )
                    }

                    // Ruta vial azul en el mapa
                    if (routeResult.polylinePoints.size >= 2) {
                        Polyline(
                            points = routeResult.polylinePoints,
                            color = Color(0xFF1565C0),
                            width = 12f
                        )
                    }
                }

                // Banner superior de inicio desde ubicación actual
                userLocation?.let {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(8.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        tonalElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = useCurrentLocStart,
                                onCheckedChange = { viewModel.toggleUseCurrentLocation(it) }
                            )
                            Text(
                                text = "Iniciar recorrido desde mi ubicación actual",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }

            // Card tiempo y distancia real
            if (routeResult.totalDurationText.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(4.dp))
                            Column {
                                Text("Tiempo de viaje", style = MaterialTheme.typography.labelSmall)
                                Text(routeResult.totalDurationText,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        HorizontalDivider(modifier = Modifier.height(35.dp).width(1.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Route, contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary)
                            Spacer(Modifier.width(4.dp))
                            Column {
                                Text("Distancia total", style = MaterialTheme.typography.labelSmall)
                                Text(routeResult.totalDistanceText,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }
                }
            }

            Text("Itinerario Día por Día:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Aún no tienes destinos en tu itinerario",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Presiona el botón '+' para buscar y agregar tus destinos.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items) { item ->
                        ItineraryCard(
                            item = item,
                            onItemClick = {
                                selectedPreviewItem = item
                                coroutineScope.launch {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(LatLng(item.latitude, item.longitude), 14f)
                                    )
                                }
                            },
                            onDeleteClick = { viewModel.removeItemFromItinerary(item.id) }
                        )
                    }
                }
            }
        }
    }

    // ── Tarjeta flotante de Vista Previa con Foto de Referencia (estilo Google Maps) ──
    selectedPreviewItem?.let { previewItem ->
        PlacePreviewDialog(
            item = previewItem,
            onDismiss = { selectedPreviewItem = null }
        )
    }

    if (showAddDialog) {
        AddPlaceDialog(
            viewModel = viewModel,
            onDismiss = {
                viewModel.clearSelectedPlace()
                showAddDialog = false
            },
            onConfirm = { title, loc, lat, lng, cost, day ->
                viewModel.addPlaceToItinerary(title, loc, lat, lng, cost, day)
                showAddDialog = false
            }
        )
    }
}

// ─────────────────────────────────────────────
// Diálogo de Previsualización con Foto
// ─────────────────────────────────────────────
@Composable
fun PlacePreviewDialog(
    item: ItineraryItem,
    onDismiss: () -> Unit
) {
    val photoUrl = getPlaceReferencePhoto(item.locationName, item.title)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) { Text("Entendido") }
        },
        title = {
            Text(text = "Día ${item.dayNumber}: ${item.title}", style = MaterialTheme.typography.titleMedium)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = item.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(4.dp))
                    Text(item.locationName, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                }

                Text(item.description, style = MaterialTheme.typography.bodyMedium)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Costo estimado:", style = MaterialTheme.typography.labelLarge)
                    Text("S/ ${item.cost}", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    )
}

// ─────────────────────────────────────────────
// Generador de Fotos de Referencia Turísticas
// ─────────────────────────────────────────────
fun getPlaceReferencePhoto(locationName: String, title: String): String {
    val text = "$locationName $title".lowercase()
    return when {
        text.contains("uros") -> "https://images.unsplash.com/photo-1589308078059-be1415eab4c3?w=800"
        text.contains("taquile") -> "https://images.unsplash.com/photo-1526392060635-9d6019884377?w=800"
        text.contains("amantani") || text.contains("amantaní") -> "https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?w=800"
        text.contains("sillustani") -> "https://images.unsplash.com/photo-1589308078059-be1415eab4c3?w=800"
        text.contains("chucuito") || text.contains("inka uyo") -> "https://images.unsplash.com/photo-1531968455001-5c5272a41129?w=800"
        text.contains("aramu") || text.contains("muru") -> "https://images.unsplash.com/photo-1518638150340-f706e86654de?w=800"
        text.contains("ilave") -> "https://images.unsplash.com/photo-1526392060635-9d6019884377?w=800"
        text.contains("juliaca") -> "https://images.unsplash.com/photo-1589308078059-be1415eab4c3?w=800"
        else -> "https://images.unsplash.com/photo-1526392060635-9d6019884377?w=800"
    }
}

// ─────────────────────────────────────────────
// Diálogo con buscador + destinos populares
// ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlaceDialog(
    viewModel: ItineraryViewModel,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double, Double, Double, Int) -> Unit
) {
    val suggestions by viewModel.searchSuggestions.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val selectedPlace by viewModel.selectedPlace.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var costStr by remember { mutableStateOf("") }
    var dayStr by remember { mutableStateOf("1") }

    val popularPlaces = listOf(
        Triple("Islas Uros", -15.8239, -69.9691),
        Triple("Isla Taquile", -15.7725, -69.6881),
        Triple("Isla Amantaní", -15.6667, -69.7083),
        Triple("Chullpas de Sillustani", -15.7196, -70.1264),
        Triple("Chucuito - Inka Uyo", -15.8942, -69.8894),
        Triple("Portal Aramu Muru", -16.0378, -69.4189),
        Triple("Mirador Kuntur Wasi", -15.8298, -70.0245),
        Triple("Puerto de Puno", -15.8361, -70.0158)
    )
    var selectedPopular by remember { mutableStateOf<Triple<String, Double, Double>?>(null) }

    val canSave = (selectedTab == 0 && selectedPlace != null) ||
                  (selectedTab == 1 && selectedPopular != null)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Destino Turístico") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0; selectedPopular = null },
                        text = { Text("Buscar lugar") },
                        icon = { Icon(Icons.Default.Search, contentDescription = null,
                            modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            viewModel.clearSelectedPlace()
                            searchQuery = ""
                        },
                        text = { Text("Populares") },
                        icon = { Icon(Icons.Default.Star, contentDescription = null,
                            modifier = Modifier.size(16.dp)) }
                    )
                }

                if (selectedTab == 0) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            viewModel.onSearchQueryChanged(it)
                        },
                        label = { Text("Escribe el nombre del lugar") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (isSearching) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (suggestions.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column {
                                suggestions.take(5).forEach { suggestion ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                searchQuery = suggestion.description
                                                viewModel.onSuggestionSelected(suggestion)
                                            }
                                            .padding(horizontal = 16.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.LocationOn, contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text(suggestion.description,
                                            style = MaterialTheme.typography.bodyMedium)
                                    }
                                    if (suggestion != suggestions.take(5).last()) {
                                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                                    }
                                }
                            }
                        }
                    }

                    if (selectedPlace != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Row(modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary)
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text("✅ Lugar reconocido:",
                                        style = MaterialTheme.typography.labelSmall)
                                    Text(selectedPlace!!.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.secondary)
                                }
                            }
                        }
                    }

                    if (searchQuery.length >= 2 && !isSearching &&
                        suggestions.isEmpty() && selectedPlace == null) {
                        Text(
                            "⚠️ Sin resultados. Verifica tu conexión o prueba 'Populares'.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                if (selectedTab == 1) {
                    Text("Selecciona un destino:",
                        style = MaterialTheme.typography.labelLarge)
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        popularPlaces.forEach { place ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedPopular = place }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedPopular == place,
                                    onClick = { selectedPopular = place }
                                )
                                Icon(Icons.Default.LocationOn, contentDescription = null,
                                    tint = if (selectedPopular == place)
                                        MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(place.first,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (selectedPopular == place)
                                        MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = costStr,
                    onValueChange = { costStr = it },
                    label = { Text("Costo Estimado (S/)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = dayStr,
                    onValueChange = { dayStr = it },
                    label = { Text("Número de Día (1, 2, 3...)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cost = costStr.toDoubleOrNull() ?: 0.0
                    val day = dayStr.toIntOrNull() ?: 1
                    if (selectedTab == 0 && selectedPlace != null) {
                        val place = selectedPlace!!
                        onConfirm(place.name, place.name,
                            place.latLng.latitude, place.latLng.longitude, cost, day)
                    } else if (selectedTab == 1 && selectedPopular != null) {
                        val place = selectedPopular!!
                        onConfirm(place.first, place.first, place.second, place.third, cost, day)
                    }
                },
                enabled = canSave
            ) {
                Text("Guardar en Itinerario")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

// ─────────────────────────────────────────────
// Tarjeta del itinerario (click abre vista previa)
// ─────────────────────────────────────────────
@Composable
fun ItineraryCard(
    item: ItineraryItem,
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Badge(containerColor = MaterialTheme.colorScheme.primary) {
                Text("Día ${item.dayNumber}", modifier = Modifier.padding(6.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null,
                        modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(item.locationName, style = MaterialTheme.typography.bodySmall)
                }
            }
            Text("S/ ${item.cost}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}