package com.proyect.travelhub.ui.screens.servicedetail

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.proyect.travelhub.data.repository.ReservationRepository

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
fun ServiceDetailScreen(
    serviceId: String,
    onBack: () -> Unit,
    onChatClick: (
        chatId: String,
        providerId: String,
        providerName: String
    ) -> Unit,
    viewModel: ServiceDetailViewModel = viewModel()
) {

    LaunchedEffect(serviceId) {
        viewModel.loadService(serviceId)
    }

    val service by viewModel.service.collectAsState()
    val loading by viewModel.loading.collectAsState()

    if (loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TiticacaSky),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = TiticacaTurquoise, strokeWidth = 4.dp)
        }
        return
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detalle del Servicio",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TiticacaDeepBlue
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(TiticacaSky)
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            // Imagen hero del servicio
            val heroImage = service?.imageUrls?.firstOrNull()?.takeIf { it.isNotBlank() }
                ?: "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800"

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                AsyncImage(
                    model = heroImage,
                    contentDescription = service?.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Gradiente inferior para legibilidad
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    TiticacaDeepBlue.copy(alpha = 0.7f)
                                )
                            )
                        )
                )
                // Título sobre la imagen
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
                ) {
                    Text(
                        text = service?.title ?: "Cargando...",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Place,
                            contentDescription = null,
                            tint = TiticacaGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = service?.location ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            // Contenido principal en card blanca
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-20).dp)
                    .padding(horizontal = 16.dp)
                    .shadow(8.dp, RoundedCornerShape(24.dp), clip = false),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceSoft),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    // Fila de rating y categoría
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = TiticacaSky
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = TiticacaGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = service?.rating?.toString() ?: "--",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TiticacaDeepBlue
                                    )
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = TiticacaTurquoise.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = service?.category?.name ?: "Servicio",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TiticacaTurquoise
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Precio destacado
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = TiticacaDeepBlue,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Precio por día / unidad",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "S/ ${service?.pricePerDayOrUnit ?: "--"}",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TiticacaGold
                                    )
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.AttachMoney,
                                contentDescription = null,
                                tint = TiticacaGold.copy(alpha = 0.5f),
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Descripción
                    Text(
                        text = "Descripción",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TiticacaDeepBlue
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = service?.description ?: "Sin descripción disponible.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    HorizontalDivider(color = TiticacaBlue.copy(alpha = 0.15f))

                    Spacer(modifier = Modifier.height(24.dp))

                    // ID del servicio (info técnica sutil)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = TiticacaSky
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Numbers,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ID: $serviceId",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón Chatear (lógica original preservada)
                    Button(
                        onClick = {
                            val currentService = service ?: return@Button
                            val chatId =
                                if (currentService.providerId.isNotBlank())
                                    currentService.providerId
                                else
                                    currentService.id
                            onChatClick(
                                chatId,
                                currentService.providerId,
                                currentService.providerName
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .shadow(6.dp, RoundedCornerShape(16.dp), clip = false),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TiticacaTurquoise,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Chatear con el prestador",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Botón Reservar (lógica original preservada)
                    OutlinedButton(
                        onClick = {
                            service?.let {
                                ReservationRepository.addReservation(it)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            TiticacaBlue.copy(alpha = 0.4f)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TiticacaDeepBlue
                        )
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Reservar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}