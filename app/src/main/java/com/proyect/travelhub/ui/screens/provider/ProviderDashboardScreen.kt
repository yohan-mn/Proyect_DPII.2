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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.proyect.travelhub.data.model.ServiceCategory
import com.proyect.travelhub.data.model.ServiceItem

// ---------------------------------------------------------------------------
// Paleta Lago Titicaca (coherente con LoginScreen)
// ---------------------------------------------------------------------------
private val TiticacaDeepBlue = Color(0xFF0D3B66)
private val TiticacaBlue   = Color(0xFF1976D2)
private val TiticacaTurquoise = Color(0xFF14B8A6)
private val TiticacaSky    = Color(0xFFE3F2FD)
private val TiticacaGold   = Color(0xFFF2A93B)
private val TiticacaGoldDark = Color(0xFFD98324)
private val SurfaceSoft    = Color(0xFFFFFFFF)
private val TextMuted      = Color(0xFF5B6B79)

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
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TiticacaDeepBlue
                ),
                actions = {
                    IconButton(onClick = onNavigateToChatList) {
                        Icon(Icons.Default.Chat, contentDescription = "Mis Chats", tint = Color.White)
                    }
                    IconButton(onClick = { viewModel.loadMyServices() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = Color.White)
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Mi Perfil", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceSoft,
                tonalElevation = 2.dp
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    label = { Text("Mis Servicios") },
                    icon = { Icon(Icons.Default.Storefront, contentDescription = null) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TiticacaTurquoise,
                        selectedTextColor = TiticacaTurquoise,
                        indicatorColor = TiticacaSky
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToChatList,
                    label = { Text("Mensajes / Chat") },
                    icon = { Icon(Icons.Default.Chat, contentDescription = null) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TiticacaTurquoise,
                        selectedTextColor = TiticacaTurquoise,
                        indicatorColor = TiticacaSky
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToProfile,
                    label = { Text("Perfil") },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TiticacaTurquoise,
                        selectedTextColor = TiticacaTurquoise,
                        indicatorColor = TiticacaSky
                    )
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddServiceDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Publicar Servicio", fontWeight = FontWeight.SemiBold) },
                containerColor = TiticacaGold,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.shadow(8.dp, RoundedCornerShape(16.dp), clip = false)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(TiticacaSky, Color.White)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Header card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(20.dp), clip = false),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = TiticacaDeepBlue
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Tus Servicios Turísticos Publicados",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Gestiona y publica nuevos servicios para turistas del Lago Titicaca",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TiticacaTurquoise)
                    }
                } else if (myServices.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(
                                shape = RoundedCornerShape(24.dp),
                                color = TiticacaSky,
                                modifier = Modifier.size(80.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Store,
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp),
                                        tint = TiticacaBlue
                                    )
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Aún no has publicado servicios turísticos.",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TiticacaDeepBlue
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "Toca en '+ Publicar Servicio' para agregar tu hotel, restaurante o tour.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
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
            .shadow(4.dp, RoundedCornerShape(16.dp), clip = false),
        shape = RoundedCornerShape(16.dp),
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
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = service.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TiticacaDeepBlue
                )

                Spacer(Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = TiticacaSky
                ) {
                    Text(
                        service.category.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = TiticacaDeepBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(6.dp))
                Text(
                    service.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    color = TextMuted
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "S/ ${service.pricePerDayOrUnit} / día • ${service.location}",
                    style = MaterialTheme.typography.labelLarge,
                    color = TiticacaTurquoise,
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
        uri?.let {
            imageUrl = it.toString()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceSoft,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                "Publicar Servicio Turístico",
                fontWeight = FontWeight.Bold,
                color = TiticacaDeepBlue
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Categoría del Servicio:",
                    style = MaterialTheme.typography.labelLarge.copy(color = TiticacaDeepBlue)
                )

                Column {
                    ServiceCategory.values().forEach { category ->
                        val label = when (category) {
                            ServiceCategory.HOSPEDAJE -> "🏨 Hospedaje / Hotel"
                            ServiceCategory.ALIMENTACION -> "🍽️ Restaurante / Gastronomía"
                            ServiceCategory.GUIA -> "🧭 Guía Turístico"
                            ServiceCategory.TRANSPORTE -> "🚤 Transporte Privado / Tour"
                            ServiceCategory.TRADUCCION -> "🗣️ Servicio de Traducción"
                        }
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
                                    selectedColor = TiticacaTurquoise,
                                    unselectedColor = TextMuted
                                )
                            )
                            Text(label, style = MaterialTheme.typography.bodyMedium, color = TiticacaDeepBlue)
                        }
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título / Nombre del Establecimiento") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(

                        focusedBorderColor = TiticacaTurquoise,
                        unfocusedBorderColor = TiticacaBlue.copy(alpha = 0.25f),

                        focusedLabelColor = TiticacaTurquoise,
                        unfocusedLabelColor = TextMuted,

                        focusedTextColor = TiticacaDeepBlue,
                        unfocusedTextColor = TiticacaDeepBlue,

                        focusedPlaceholderColor = TextMuted,
                        unfocusedPlaceholderColor = TextMuted,

                        cursorColor = TiticacaTurquoise
                    )
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Descripción de los servicios") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(

                        focusedBorderColor = TiticacaTurquoise,
                        unfocusedBorderColor = TiticacaBlue.copy(alpha = 0.25f),

                        focusedLabelColor = TiticacaTurquoise,
                        unfocusedLabelColor = TextMuted,

                        focusedTextColor = TiticacaDeepBlue,
                        unfocusedTextColor = TiticacaDeepBlue,

                        focusedPlaceholderColor = TextMuted,
                        unfocusedPlaceholderColor = TextMuted,

                        cursorColor = TiticacaTurquoise
                    )
                )

                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Precio estimado (S/)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(

                        focusedBorderColor = TiticacaTurquoise,
                        unfocusedBorderColor = TiticacaBlue.copy(alpha = 0.25f),

                        focusedLabelColor = TiticacaTurquoise,
                        unfocusedLabelColor = TextMuted,

                        focusedTextColor = TiticacaDeepBlue,
                        unfocusedTextColor = TiticacaDeepBlue,

                        focusedPlaceholderColor = TextMuted,
                        unfocusedPlaceholderColor = TextMuted,

                        cursorColor = TiticacaTurquoise
                    )
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Ubicación (ej: Puno Centro, Uros)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(

                        focusedBorderColor = TiticacaTurquoise,
                        unfocusedBorderColor = TiticacaBlue.copy(alpha = 0.25f),

                        focusedLabelColor = TiticacaTurquoise,
                        unfocusedLabelColor = TextMuted,

                        focusedTextColor = TiticacaDeepBlue,
                        unfocusedTextColor = TiticacaDeepBlue,

                        focusedPlaceholderColor = TextMuted,
                        unfocusedPlaceholderColor = TextMuted,

                        cursorColor = TiticacaTurquoise
                    )
                )

                Text(
                    "Fotografía del Establecimiento/Lugar:",
                    style = MaterialTheme.typography.labelLarge.copy(color = TiticacaDeepBlue)
                )

                if (imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Previsualización",
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
                        containerColor = TiticacaBlue,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Seleccionar de Galería")
                }

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("O pega una URL de foto") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(

                        focusedBorderColor = TiticacaTurquoise,
                        unfocusedBorderColor = TiticacaBlue.copy(alpha = 0.25f),

                        focusedLabelColor = TiticacaTurquoise,
                        unfocusedLabelColor = TextMuted,

                        focusedTextColor = TiticacaDeepBlue,
                        unfocusedTextColor = TiticacaDeepBlue,

                        focusedPlaceholderColor = TextMuted,
                        unfocusedPlaceholderColor = TextMuted,

                        cursorColor = TiticacaTurquoise
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
                    containerColor = TiticacaGold,
                    contentColor = Color.White,
                    disabledContainerColor = TiticacaGold.copy(alpha = 0.5f)
                )
            ) {
                Text("Publicar en Catálogo", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextMuted)
            }
        }
    )
}