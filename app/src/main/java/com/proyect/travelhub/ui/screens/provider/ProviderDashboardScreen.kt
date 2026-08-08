package com.proyect.travelhub.ui.screens.provider

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.proyect.travelhub.data.model.ServiceCategory
import com.proyect.travelhub.data.model.ServiceItem

// ---------------------------------------------------------------------------
// Paleta inspirada en las referencias (salon/barberia Casca)
// ---------------------------------------------------------------------------
private val PrimaryOrange      = Color(0xFFF5A623)
private val PrimaryOrangeLight = Color(0xFFFFF3E0)
private val PrimaryOrangeDark  = Color(0xFFE8941F)
private val Background         = Color(0xFFE3F2FD)
private val SurfaceSoft        = Color(0xFFFFFFFF)
private val InputBg            = Color(0xFFF5F5F5)
private val TextPrimary        = Color(0xFF1F1F1F)
private val TextSecondary      = Color(0xFF9E9E9E)

// ---------------------------------------------------------------------------
// Iconos y etiquetas por categoria de servicio
// ---------------------------------------------------------------------------
private fun categoryIcon(category: ServiceCategory): ImageVector = when (category) {
    ServiceCategory.HOSPEDAJE     -> Icons.Default.Hotel
    ServiceCategory.ALIMENTACION  -> Icons.Default.Restaurant
    ServiceCategory.GUIA          -> Icons.Default.TravelExplore
    ServiceCategory.TRANSPORTE    -> Icons.Default.DirectionsCar
    ServiceCategory.TRADUCCION    -> Icons.Default.Translate
}

private fun categoryLabel(category: ServiceCategory): String = when (category) {
    ServiceCategory.HOSPEDAJE     -> "Hospedaje / Hotel"
    ServiceCategory.ALIMENTACION  -> "Restaurante / Gastronomia"
    ServiceCategory.GUIA          -> "Guia Turistico"
    ServiceCategory.TRANSPORTE    -> "Transporte Privado / Tour"
    ServiceCategory.TRADUCCION    -> "Servicio de Traduccion"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderDashboardScreen(
    onNavigateToCatalog: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToChatList: () -> Unit = {},
    viewModel: ProviderDashboardViewModel = viewModel()
) {
    val myServices by viewModel.myServices.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showAddServiceDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Panel del Prestador",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceSoft
                ),
                actions = {
                    IconButton(onClick = onNavigateToChatList) {
                        Icon(Icons.Default.Chat, contentDescription = "Mis Chats", tint = TextPrimary)
                    }
                    IconButton(onClick = { viewModel.loadMyServices() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = TextPrimary)
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Mi Perfil", tint = TextPrimary)
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
                    label = { Text("Mis Servicios") },
                    icon = { Icon(Icons.Default.Storefront, contentDescription = null) },
                    colors = navColors
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToChatList,
                    label = { Text("Mensajes / Chat") },
                    icon = { Icon(Icons.Default.Chat, contentDescription = null) },
                    colors = navColors
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToProfile,
                    label = { Text("Perfil") },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    colors = navColors
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddServiceDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Publicar Servicio", fontWeight = FontWeight.SemiBold) },
                containerColor = PrimaryOrange,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.shadow(8.dp, RoundedCornerShape(16.dp), clip = false)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Banner naranja estilo referencia
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(20.dp), clip = false),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryOrange)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Tus Servicios Turisticos Publicados",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Gestiona y publica nuevos servicios para turistas del Lago Titicaca",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryOrange)
                }
            } else if (myServices.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = PrimaryOrangeLight,
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Store,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = PrimaryOrange
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Aun no has publicado servicios turisticos.",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Toca en '+ Publicar Servicio' para agregar tu hotel, restaurante o tour.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(myServices) { service ->
                        ProviderServiceCard(service = service)
                    }
                }
            }
        }
    }

    if (showAddServiceDialog) {
        AddServiceDialog(
            onDismiss = { showAddServiceDialog = false },
            onConfirm = { title, desc, category, price, loc, imgUrl ->
                viewModel.publishService(title, desc, category, price, loc, imgUrl)
                showAddServiceDialog = false
            }
        )
    }
}

@Composable
fun ProviderServiceCard(service: ServiceItem) {
    val photoUrl = if (service.imageUrls.isNotEmpty() && service.imageUrls.first().isNotBlank()) {
        service.imageUrls.first()
    } else {
        "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp), clip = false),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = photoUrl,
                contentDescription = service.title,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = service.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )

                Spacer(Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryOrangeLight
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = categoryIcon(service.category),
                            contentDescription = null,
                            tint = PrimaryOrangeDark,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            categoryLabel(service.category),
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryOrangeDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(Modifier.height(6.dp))
                Text(
                    service.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    color = TextSecondary
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "S/ ${service.pricePerDayOrUnit} / dia \u2022 ${service.location}",
                    style = MaterialTheme.typography.labelLarge,
                    color = PrimaryOrange,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddServiceDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, ServiceCategory, Double, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Puno Centro") }
    var imageUrl by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ServiceCategory.HOSPEDAJE) }

    val galleryLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let { imageUrl = it.toString() }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceSoft,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                "Publicar Servicio Turistico",
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Categoria del Servicio:",
                    style = MaterialTheme.typography.labelLarge.copy(color = TextPrimary)
                )

                Column {
                    ServiceCategory.values().forEach { category ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedCategory = category }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = selectedCategory == category,
                                onClick = { selectedCategory = category },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = PrimaryOrange,
                                    unselectedColor = TextSecondary
                                )
                            )
                            Icon(
                                imageVector = categoryIcon(category),
                                contentDescription = null,
                                tint = PrimaryOrange,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                categoryLabel(category),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary
                            )
                        }
                    }
                }

                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titulo / Nombre del Establecimiento") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = InputBg,
                        unfocusedContainerColor = InputBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = PrimaryOrange
                    )
                )

                TextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Descripcion de los servicios") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = InputBg,
                        unfocusedContainerColor = InputBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = PrimaryOrange
                    )
                )

                TextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Precio estimado (S/)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = InputBg,
                        unfocusedContainerColor = InputBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = PrimaryOrange
                    )
                )

                TextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Ubicacion (ej: Puno Centro, Uros)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = InputBg,
                        unfocusedContainerColor = InputBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = PrimaryOrange
                    )
                )

                Text(
                    "Fotografia del Establecimiento/Lugar:",
                    style = MaterialTheme.typography.labelLarge.copy(color = TextPrimary)
                )

                if (imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Previsualizacion",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Button(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryOrange,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Seleccionar de Galeria")
                }

                TextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("O pega una URL de foto") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = InputBg,
                        unfocusedContainerColor = InputBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = PrimaryOrange
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceStr.toDoubleOrNull() ?: 0.0
                    onConfirm(title, desc, selectedCategory, price, location, imageUrl)
                },
                enabled = title.isNotBlank(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryOrange,
                    contentColor = Color.White,
                    disabledContainerColor = PrimaryOrange.copy(alpha = 0.5f)
                )
            ) {
                Text("Publicar en Catalogo", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        }
    )
}