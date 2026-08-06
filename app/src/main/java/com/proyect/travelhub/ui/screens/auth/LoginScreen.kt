package com.proyect.travelhub.ui.screens.auth

import android.widget.Toast
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.proyect.travelhub.data.model.User

// ---------------------------------------------------------------------------
// Paleta inspirada en el Lago Titicaca: azules profundos, turquesas y un
// acento cálido dorado (totora / atardecer andino) para los botones.
// Solo se usa para dar un aspecto propio; no altera ninguna lógica.
// ---------------------------------------------------------------------------
private val TiticacaDeepBlue = Color(0xFF0D3B66)
private val TiticacaBlue = Color(0xFF1976D2)
private val TiticacaTurquoise = Color(0xFF14B8A6)
private val TiticacaSky = Color(0xFFE3F2FD)
private val TiticacaGold = Color(0xFFF2A93B)
private val TiticacaGoldDark = Color(0xFFD98324)
private val SurfaceSoft = Color(0xFFFFFFFF)
private val TextMuted = Color(0xFF5B6B79)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (User) -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var showForgotDialog by remember { mutableStateOf(false) }
    var resetEmailInput by remember { mutableStateOf("") }
    var isResetLoading by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess((authState as AuthState.Success).user)
        }
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(

        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,

        focusedPlaceholderColor = TextMuted,
        unfocusedPlaceholderColor = TextMuted,

        focusedLabelColor = TiticacaTurquoise,
        unfocusedLabelColor = TextMuted,

        focusedBorderColor = TiticacaTurquoise,
        unfocusedBorderColor = TiticacaBlue.copy(alpha = 0.25f),

        focusedLeadingIconColor = TiticacaTurquoise,
        unfocusedLeadingIconColor = TextMuted,
        focusedTrailingIconColor = TiticacaTurquoise,
        unfocusedTrailingIconColor = TextMuted,

        cursorColor = TiticacaTurquoise
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        TiticacaDeepBlue,
                        TiticacaBlue,
                        TiticacaSky
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Logo Icon Container
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.Transparent,
                modifier = Modifier
                    .size(88.dp)
                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(24.dp), clip = false)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(TiticacaTurquoise, TiticacaBlue)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.TravelExplore,
                        contentDescription = "Logo",
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "TravelHub",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 36.sp,
                    letterSpacing = 0.5.sp
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Explora el Lago Titicaca y gestiona tus viajes",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Card Form Container
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
                        .padding(horizontal = 22.dp, vertical = 26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Iniciar Sesión",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        ),
                        color = TiticacaDeepBlue
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo Electrónico") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        shape = RoundedCornerShape(16.dp),
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(16.dp),
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Olvidé mi contraseña
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        TextButton(onClick = {
                            resetEmailInput = email
                            showForgotDialog = true
                        }) {
                            Text(
                                "¿Olvidaste tu contraseña?",
                                style = MaterialTheme.typography.bodySmall,
                                color = TiticacaBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(color = TiticacaTurquoise)
                    } else {
                        Button(
                            onClick = { viewModel.login(email, password) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp), clip = false),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TiticacaBlue,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Iniciar Sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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

                    Spacer(modifier = Modifier.height(20.dp))

                    HorizontalDivider(color = TextMuted.copy(alpha = 0.2f))

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botón Continuar con Google
                    val activity = context as? android.app.Activity
                    val googleSignInLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                        contract = androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
                    ) { result ->
                        if (result.resultCode == android.app.Activity.RESULT_OK) {
                            val task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(result.data)
                            try {
                                val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
                                val idToken = account?.idToken
                                if (idToken != null) {
                                    viewModel.loginWithGoogleIdToken(idToken)
                                } else {
                                    Toast.makeText(context, "No se pudo obtener el Token de Google", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error de autenticación Google: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(
                                com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
                            )
                                .requestIdToken(context.getString(com.proyect.travelhub.R.string.default_web_client_id))
                                .requestEmail()
                                .build()

                            val googleSignInClient = com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(context, gso)
                            // Forzar pedir selección de cuenta
                            googleSignInClient.signOut().addOnCompleteListener {
                                val signInIntent = googleSignInClient.signInIntent
                                googleSignInLauncher.launch(signInIntent)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TiticacaBlue.copy(alpha = 0.35f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TiticacaDeepBlue)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = TiticacaBlue
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Continuar con Google (Gmail)", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = onNavigateToRegister) {
                Text(
                    "¿No tienes cuenta? Crear una cuenta gratis",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    // Diálogo Olvidaste tu contraseña
    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            containerColor = SurfaceSoft,
            shape = RoundedCornerShape(24.dp),
            icon = {
                Icon(
                    imageVector = Icons.Default.LockReset,
                    contentDescription = null,
                    tint = TiticacaTurquoise
                )
            },
            title = {
                Text(
                    "Recuperar Contraseña",
                    fontWeight = FontWeight.Bold,
                    color = TiticacaDeepBlue
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Ingresa tu correo registrado y te enviaremos una clave/enlace para restablecer tu contraseña.",
                        color = TextMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = resetEmailInput,
                        onValueChange = { resetEmailInput = it },
                        label = { Text("Correo Electrónico") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),

                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,

                            focusedPlaceholderColor = TextMuted,
                            unfocusedPlaceholderColor = TextMuted,

                            focusedBorderColor = TiticacaTurquoise,
                            unfocusedBorderColor = TiticacaBlue.copy(alpha = 0.25f),

                            focusedLabelColor = TiticacaTurquoise,
                            unfocusedLabelColor = TextMuted,

                            cursorColor = TiticacaTurquoise
                        ),

                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isResetLoading = true
                        viewModel.sendPasswordReset(resetEmailInput) { success, message ->
                            isResetLoading = false
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            if (success) {
                                showForgotDialog = false
                            }
                        }
                    },
                    enabled = !isResetLoading,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TiticacaGold,
                        contentColor = Color.White,
                        disabledContainerColor = TiticacaGold.copy(alpha = 0.5f)
                    )
                ) {
                    Text(if (isResetLoading) "Enviando..." else "Enviar Correo")
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotDialog = false }) {
                    Text("Cancelar", color = TextMuted)
                }
            }
        )
    }
}