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
import androidx.compose.ui.draw.shadow
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
import androidx.compose.ui.text.font.FontWeight

// ---------------------------------------------------------------------------
// Paleta inspirada en las referencias (salón/barbería Casca)
// ---------------------------------------------------------------------------
private val PrimaryOrange      = Color(0xFFF5A623)
private val PrimaryOrangeLight = Color(0xFFFFF3E0)
private val PrimaryOrangeDark  = Color(0xFFE8941F)
private val Background         = Color(0xFFE3F2FD)
private val SurfaceSoft        = Color(0xFFFFFFFF)
private val InputBg            = Color(0xFFF5F5F5)
private val TextPrimary        = Color(0xFF1F1F1F)
private val TextSecondary      = Color(0xFF9E9E9E)
private val ErrorRed           = Color(0xFFE53935)

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
    var selectedPreviewItem by remember { mutableStateOf<ItineraryItem?>(null) }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Mi Itinerario y Ruta", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceSoft,
                    titleContentColor = TextPrimary,
                    actionIconContentColor = TextPrimary
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
                            contentDescription = "Centrar en Mi Ubicaci?n",
                            tint = PrimaryOrange
                        )
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
                NavigationBarItem(selected = false, onClick = onNavigateToCatalog, label = { Text("Catalogo") }, icon = { Icon(Icons.Default.Storefront, contentDescription = null) }, colors = navColors)
                NavigationBarItem(selected = true, onClick = { }, label = { Text("Itinerario") }, icon = { Icon(Icons.Default.Map, contentDescription = null) }, colors = navColors)
                NavigationBarItem(selected = false, onClick = onNavigateToCalculator, label = { Text("Calculadora") }, icon = { Icon(Icons.Default.Calculate, contentDescription = null) }, colors = navColors)
                NavigationBarItem(selected = false, onClick = onNavigateToChatList, label = { Text("Reservas / Chat") }, icon = { Icon(Icons.Default.Chat, contentDescription = null) }, colors = navColors)
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PrimaryOrange,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar lugar")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        mapType = MapType.NORMAL,
                        isMyLocationEnabled = userLocation != null
                    )
                ) {
                    userLocation?.let { loc ->
                        Marker(
                            state = MarkerState(position = loc),
                            title = "? Mi Ubicacion Actual",
                            snippet = "Punto de inicio del recorrido"
                        )
                    }

                    items.forEach { item ->
                        Marker(
                            state = MarkerState(position = LatLng(item.latitude, item.longitude)),
                            title = "Dia ${item.dayNumber}: ${item.title}",
                            snippet = "Toca para ver foto e informacion",
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

                    if (routeResult.polylinePoints.size >= 2) {
                        Polyline(
                            points = routeResult.polylinePoints,
                            color = PrimaryOrangeDark,
                            width = 12f
                        )
                    }
                }

                userLocation?.let {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(8.dp)
                            .shadow(elevation = 3.dp, shape = RoundedCornerShape(20.dp), clip = false),
                        shape = RoundedCornerShape(20.dp),
                        color = SurfaceSoft.copy(alpha = 0.95f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = useCurrentLocStart,
                                onCheckedChange = { viewModel.toggleUseCurrentLocation(it) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = PrimaryOrange,
                                    uncheckedColor = TextSecondary
                                )
                            )
                            Text(
                                text = "Iniciar recorrido desde mi ubicacion actual",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            if (routeResult.totalDurationText.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp), clip = false),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceSoft),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = PrimaryOrange)
                            Spacer(Modifier.width(4.dp))
                            Column {
                                Text("Tiempo de viaje", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                Text(routeResult.totalDurationText, style = MaterialTheme.typography.titleMedium, color = PrimaryOrangeDark)
                            }
                        }
                        HorizontalDivider(modifier = Modifier.height(35.dp).width(1.dp), color = TextSecondary.copy(alpha = 0.2f))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Route, contentDescription = null, tint = PrimaryOrange)
                            Spacer(Modifier.width(4.dp))
                            Column {
                                Text("Distancia total", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                Text(routeResult.totalDistanceText, style = MaterialTheme.typography.titleMedium, color = PrimaryOrangeDark)
                            }
                        }
                    }
                }
            }

            Text("Itinerario Dia por Dia:",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryOrange)
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
                            tint = PrimaryOrange.copy(alpha = 0.6f)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Aun no tienes destinos en tu itinerario",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Presiona el boton '+' para buscar y agregar tus destinos.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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

@Composable
fun PlacePreviewDialog(
    item: ItineraryItem,
    onDismiss: () -> Unit
) {
    val photoUrl = getPlaceReferencePhoto(item.locationName, item.title)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceSoft,
        shape = RoundedCornerShape(24.dp),
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
            ) { Text("Entendido") }
        },
        title = {
            Text(text = "D?a ${item.dayNumber}: ${item.title}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold), color = TextPrimary)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = item.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryOrange)
                    Spacer(Modifier.width(4.dp))
                    Text(item.locationName, style = MaterialTheme.typography.titleSmall, color = PrimaryOrangeDark)
                }
                Text(item.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Costo estimado:", style = MaterialTheme.typography.labelLarge, color = TextPrimary)
                    Text("S/ ${item.cost}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold), color = PrimaryOrange)
                }
            }
        }
    )
}

fun getPlaceReferencePhoto(locationName: String, title: String): String {
    val text = "$locationName $title".lowercase()
    return when {
        text.contains("uros") -> "https://images.unsplash.com/photo-1589308078059-be1415eab4c3?w=800"
        text.contains("taquile") -> "https://images.unsplash.com/photo-1526392060635-9d6019884377?w=800"
        text.contains("amantani") || text.contains("amantan?") -> "https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?w=800"
        text.contains("sillustani") -> "https://images.unsplash.com/photo-1589308078059-be1415eab4c3?w=800"
        text.contains("chucuito") || text.contains("inka uyo") -> "https://images.unsplash.com/photo-1531968455001-5c5272a41129?w=800"
        text.contains("aramu") || text.contains("muru") -> "https://images.unsplash.com/photo-1518638150340-f706e86654de?w=800"
        text.contains("ilave") -> "https://images.unsplash.com/photo-1526392060635-9d6019884377?w=800"
        text.contains("juliaca") -> "https://images.unsplash.com/photo-1589308078059-be1415eab4c3?w=800"
        else -> "https://images.unsplash.com/photo-1526392060635-9d6019884377?w=800"
    }
}

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
        Triple("Isla Amantan", -15.6667, -69.7083),
        Triple("Chullpas de Sillustani", -15.7196, -70.1264),
        Triple("Chucuito - Inka Uyo", -15.8942, -69.8894),
        Triple("Portal Aramu Muru", -16.0378, -69.4189),
        Triple("Mirador Kuntur Wasi", -15.8298, -70.0245),
        Triple("Puerto de Puno", -15.8361, -70.0158)
    )
    var selectedPopular by remember { mutableStateOf<Triple<String, Double, Double>?>(null) }

    val canSave = (selectedTab == 0 && selectedPlace != null) || (selectedTab == 1 && selectedPopular != null)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceSoft,
        shape = RoundedCornerShape(24.dp),
        title = { Text("Agregar Destino Tur?stico", color = TextPrimary, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = PrimaryOrange
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0; selectedPopular = null },
                        text = { Text("Buscar lugar") },
                        icon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1; viewModel.clearSelectedPlace(); searchQuery = "" },
                        text = { Text("Populares") },
                        icon = { Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }

                if (selectedTab == 0) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it; viewModel.onSearchQueryChanged(it) },
                        label = { Text("Escribe el nombre del lugar") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryOrange) },
                        trailingIcon = { if (isSearching) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = PrimaryOrange) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = InputBg,
                            unfocusedContainerColor = InputBg,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = PrimaryOrange
                        )
                    )

                    if (suggestions.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = InputBg)
                        ) {
                            Column {
                                suggestions.take(5).forEach { suggestion ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { searchQuery = suggestion.description; viewModel.onSuggestionSelected(suggestion) }
                                            .padding(horizontal = 16.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text(suggestion.description, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                                    }
                                    if (suggestion != suggestions.take(5).last()) {
                                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = TextSecondary.copy(alpha = 0.15f))
                                    }
                                }
                            }
                        }
                    }

                    if (selectedPlace != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = PrimaryOrangeLight)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryOrange)
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text("? Lugar reconocido:", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                    Text(selectedPlace!!.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold), color = TextPrimary)
                                }
                            }
                        }
                    }

                    if (searchQuery.length >= 2 && !isSearching && suggestions.isEmpty() && selectedPlace == null) {
                        Text("?? Sin resultados. Verifica tu conexi?n o prueba 'Populares'.", style = MaterialTheme.typography.bodySmall, color = ErrorRed)
                    }
                }

                if (selectedTab == 1) {
                    Text("Selecciona un destino:", style = MaterialTheme.typography.labelLarge, color = TextPrimary)
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        popularPlaces.forEach { place ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { selectedPopular = place }.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedPopular == place,
                                    onClick = { selectedPopular = place },
                                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryOrange, unselectedColor = TextSecondary)
                                )
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = if (selectedPopular == place) PrimaryOrange else TextSecondary, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(place.first, style = MaterialTheme.typography.bodyMedium, color = if (selectedPopular == place) TextPrimary else TextSecondary)
                            }
                        }
                    }
                }

                TextField(
                    value = costStr,
                    onValueChange = { costStr = it },
                    label = { Text("Costo Estimado (S/)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = InputBg,
                        unfocusedContainerColor = InputBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = PrimaryOrange
                    )
                )
                TextField(
                    value = dayStr,
                    onValueChange = { dayStr = it },
                    label = { Text("N?mero de D?a (1, 2, 3...)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = InputBg,
                        unfocusedContainerColor = InputBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = PrimaryOrange
                    )
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
                        onConfirm(place.name, place.name, place.latLng.latitude, place.latLng.longitude, cost, day)
                    } else if (selectedTab == 1 && selectedPopular != null) {
                        val place = selectedPopular!!
                        onConfirm(place.first, place.first, place.second, place.third, cost, day)
                    }
                },
                enabled = canSave,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
            ) {
                Text("Guardar en Itinerario")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextSecondary) }
        }
    )
}

@Composable
fun ItineraryCard(
    item: ItineraryItem,
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val photoUrl = getPlaceReferencePhoto(item.locationName, item.title)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp), clip = false)
            .clickable { onItemClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = photoUrl,
                contentDescription = item.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryOrange
                ) {
                    Text(
                        "D?a ${item.dayNumber}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    item.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = TextSecondary)
                    Spacer(Modifier.width(4.dp))
                    Text(item.locationName, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "S/ ${item.cost}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = PrimaryOrange
                )
                IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = ErrorRed, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}