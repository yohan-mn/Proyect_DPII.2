package com.proyect.travelhub.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.proyect.travelhub.data.model.UserRole

// ---------------------------------------------------------------------------
// Paleta inspirada en las referencias (salon/barberia Casca)
// ---------------------------------------------------------------------------
private val PrimaryOrange      = Color(0xFFF5A623)
private val PrimaryOrangeLight = Color(0xFFFFF3E0)
private val Background         = Color(0xFFE3F2FD)
private val SurfaceSoft        = Color(0xFFF8FDFF)
private val InputBg            = Color(0xFFF5F5F5)
private val TextPrimary        = Color(0xFF1F1F1F)
private val TextSecondary      = Color(0xFF9E9E9E)
private val ErrorRed           = Color(0xFFE53935)
private val PrimaryOrangeDark  = Color(0xFFE8941F)

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
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil de Usuario", fontWeight = FontWeight.Bold) },
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryOrange)
            }
        } else if (user != null) {
            val u = user!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(24.dp))

                // Avatar grande centrado (estilo referencia de perfil)
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(PrimaryOrangeLight),
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
                            modifier = Modifier.size(56.dp),
                            tint = PrimaryOrange
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    u.name,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    u.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    if (u.phone.isNotBlank()) u.phone else "No especificado",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(Modifier.height(8.dp))

                // Rol Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (u.role == UserRole.PRESTADOR) PrimaryOrange else PrimaryOrangeLight
                ) {
                    Text(
                        text = if (u.role == UserRole.PRESTADOR) "PRESTADOR DE SERVICIOS" else "TURISTA EXPLORADOR",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        color = if (u.role == UserRole.PRESTADOR) Color.White else PrimaryOrangeDark,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Spacer(Modifier.height(32.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                    ) {

                        ProfileMenuItem(
                            icon = Icons.Default.Person,
                            title = "Editar Perfil",
                            onClick = { showEditDialog = true }
                        )

                        HorizontalDivider(
                            color = Color(0xFFEAEAEA),
                            thickness = 1.dp
                        )

                        ProfileMenuItem(
                            icon = Icons.Default.Logout,
                            title = "Cerrar Sesión",
                            tint = ErrorRed,
                            onClick = {
                                viewModel.logout {
                                    onNavigateToLogin()
                                }
                            }
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
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
            shape = RoundedCornerShape(24.dp),
            title = { Text("Editar Perfil", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre Completo") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = InputBg,
                            unfocusedContainerColor = InputBg,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = PrimaryOrange
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    TextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Telefono") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = InputBg,
                            unfocusedContainerColor = InputBg,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = PrimaryOrange
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    TextField(
                        value = avatarUrl,
                        onValueChange = { avatarUrl = it },
                        label = { Text("URL Foto de Perfil (Opcional)") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = InputBg,
                            unfocusedContainerColor = InputBg,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = PrimaryOrange
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateProfile(name, phone, avatarUrl)
                        showEditDialog = false
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Cancelar", color = TextSecondary) }
            }
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    tint: Color = TextPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (tint == ErrorRed) ErrorRed else PrimaryOrange,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = tint,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}