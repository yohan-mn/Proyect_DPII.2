package com.proyect.travelhub.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.proyect.travelhub.data.model.UserRole

// ---------------------------------------------------------------------------
// Misma paleta usada en Login/RegisterScreen/CatalogScreen/Chat/Itinerario
// (inspirada en el Lago Titicaca) para mantener consistencia visual entre
// pantallas. No afecta la l�gica.
// ---------------------------------------------------------------------------
private val TiticacaDeepBlue = Color(0xFF0D3B66)
private val TiticacaBlue = Color(0xFF1976D2)
private val TiticacaTurquoise = Color(0xFF14B8A6)
private val TiticacaSky = Color(0xFFEAF4FB)
private val TiticacaGold = Color(0xFFF2A93B)
private val SurfaceSoft = Color(0xFFFFFFFF)
private val TextMuted = Color(0xFF5B6B79)
private val ErrorRed = Color(0xFFD64545)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val user by viewModel.userProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        containerColor = TiticacaSky,
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil de Usuario", fontWeight = FontWeight.Bold) },
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TiticacaTurquoise)
            }
        } else if (user != null) {
            val u = user!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Avatar
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(TiticacaTurquoise.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (u.avatarUrl.isNotBlank()) {
                        AsyncImage(
                            model = u.avatarUrl,
                            contentDescription = u.name,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = TiticacaTurquoise
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(u.name, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = TiticacaDeepBlue)
                Spacer(Modifier.height(4.dp))

                // Rol Badge
                Badge(
                    containerColor = if (u.role == UserRole.PRESTADOR)
                        TiticacaGold
                    else TiticacaBlue
                ) {
                    Text(
                        text = if (u.role == UserRole.PRESTADOR) "PRESTADOR DE SERVICIOS" else "TURISTA EXPLORADOR",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Datos de la cuenta en Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp), clip = false),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceSoft),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = TiticacaBlue)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Correo Electr�nico", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text(u.email, style = MaterialTheme.typography.bodyLarge, color = TiticacaDeepBlue)
                            }
                        }

                        HorizontalDivider(color = TiticacaBlue.copy(alpha = 0.15f))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = TiticacaBlue)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Tel�fono de Contacto", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text(if (u.phone.isNotBlank()) u.phone else "No especificado", style = MaterialTheme.typography.bodyLarge, color = TiticacaDeepBlue)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Botones de acci�n
                OutlinedButton(
                    onClick = { showEditDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TiticacaBlue.copy(alpha = 0.35f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TiticacaDeepBlue)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = TiticacaTurquoise)
                    Spacer(Modifier.width(8.dp))
                    Text("Editar Datos del Perfil", fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        viewModel.logout {
                            onNavigateToLogin()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Cerrar Sesi�n", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    if (showEditDialog && user != null) {
        var name by remember { mutableStateOf(user!!.name) }
        var phone by remember { mutableStateOf(user!!.phone) }
        var avatarUrl by remember { mutableStateOf(user!!.avatarUrl) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor = SurfaceSoft,
            title = { Text("Editar Perfil", color = TiticacaDeepBlue, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre Completo") },
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
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Telefono") },
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
                        value = avatarUrl,
                        onValueChange = { avatarUrl = it },
                        label = { Text("URL Foto de Perfil (Opcional)") },
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
                        viewModel.updateProfile(name, phone, avatarUrl)
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TiticacaTurquoise)
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Cancelar", color = TextMuted) }
            }
        )
    }
}