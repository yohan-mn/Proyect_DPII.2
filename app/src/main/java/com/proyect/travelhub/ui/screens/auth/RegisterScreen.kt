package com.proyect.travelhub.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.proyect.travelhub.data.model.User
import com.proyect.travelhub.data.model.UserRole

// ---------------------------------------------------------------------------
// Paleta adaptada desde las referencias visuales (salón/barbería Casca)
// ---------------------------------------------------------------------------
private val PrimaryOrange      = Color(0xFFF5A623)
private val PrimaryOrangeDark  = Color(0xFFE8941F)
private val PrimaryOrangeLight = Color(0xFFFFF3E0)
private val Background         = Color(0xFFE3F2FD)
private val SurfaceSoft        = Color(0xFFFFFFFF)
private val InputBg            = Color(0xFFF5F5F5)
private val TextPrimary        = Color(0xFF1F1F1F)
private val TextSecondary      = Color(0xFF9E9E9E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: (User) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var phone by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.TURISTA) }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onRegisterSuccess((authState as AuthState.Success).user)
        }
    }

    // Colores de campo estilo referencia
    val fieldColors = TextFieldDefaults.colors(
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        focusedContainerColor = InputBg,
        unfocusedContainerColor = InputBg,
        disabledContainerColor = InputBg,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        focusedLeadingIconColor = PrimaryOrange,
        unfocusedLeadingIconColor = TextSecondary,
        focusedTrailingIconColor = PrimaryOrange,
        unfocusedTrailingIconColor = TextSecondary,
        cursorColor = PrimaryOrange
    )

    val chipColors = FilterChipDefaults.filterChipColors(
        containerColor = InputBg,
        selectedContainerColor = PrimaryOrangeLight,
        selectedLabelColor = PrimaryOrangeDark,
        labelColor = TextSecondary
    )

    val chipBorder = FilterChipDefaults.filterChipBorder(
        enabled = true,
        selected = false,
        borderColor = Color.Transparent,
        selectedBorderColor = PrimaryOrange,
        selectedBorderWidth = 1.5.dp
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                shape = RoundedCornerShape(22.dp),
                color = Color.Transparent,
                modifier = Modifier
                    .size(76.dp)
                    .shadow(elevation = 14.dp, shape = RoundedCornerShape(22.dp), clip = false)
                    .background(PrimaryOrange, shape = RoundedCornerShape(22.dp))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "Registro",
                        tint = Color.White,
                        modifier = Modifier.size(42.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Registro en TravelHub",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.3.sp
                ),
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Crea tu cuenta como Turista o Prestador de Servicios",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 12.dp, shape = RoundedCornerShape(28.dp), clip = false),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceSoft),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre Completo") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        shape = RoundedCornerShape(16.dp),
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo Electrónico") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        shape = RoundedCornerShape(16.dp),
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña (mínimo 6 caracteres)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = TextSecondary
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(16.dp),
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Teléfono de Contacto") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        shape = RoundedCornerShape(16.dp),
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Selección de Rol (MISMA LÓGICA, ESTILO ADAPTADO)
                    Text(
                        text = "Tipo de Cuenta (Rol):",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            selected = selectedRole == UserRole.TURISTA,
                            onClick = { selectedRole = UserRole.TURISTA },
                            label = { Text("🧳 Turista", fontWeight = FontWeight.SemiBold) },
                            shape = RoundedCornerShape(14.dp),
                            colors = chipColors,
                            border = chipBorder,
                            modifier = Modifier.weight(1f)
                        )

                        FilterChip(
                            selected = selectedRole == UserRole.PRESTADOR,
                            onClick = { selectedRole = UserRole.PRESTADOR },
                            label = { Text("🏪 Prestador", fontWeight = FontWeight.SemiBold) },
                            shape = RoundedCornerShape(14.dp),
                            colors = chipColors,
                            border = chipBorder,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(color = PrimaryOrange)
                    } else {
                        Button(
                            onClick = { viewModel.register(email, password, name, selectedRole, phone) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp), clip = false),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryOrange,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Crear Cuenta", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (authState is AuthState.Error) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = (authState as AuthState.Error).message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            TextButton(onClick = onNavigateToLogin) {
                Text(
                    "¿Ya tienes una cuenta? Inicia sesión aquí",
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}