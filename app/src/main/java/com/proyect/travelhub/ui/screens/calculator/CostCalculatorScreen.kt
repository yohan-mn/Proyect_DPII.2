package com.proyect.travelhub.ui.screens.calculator

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CostCalculatorScreen(
    onNavigateToCatalog: () -> Unit,
    onNavigateToItinerary: () -> Unit,
    onNavigateToChatList: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    viewModel: CostCalculatorViewModel = viewModel()
) {
    val totalCost by viewModel.totalCost.collectAsState()
    val breakdownList by viewModel.breakdownList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Calculadora de Costos",
                        maxLines = 1,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceSoft,
                    titleContentColor = TextPrimary,
                    actionIconContentColor = TextPrimary
                ),
                actions = {
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
                    selected = false,
                    onClick = onNavigateToCatalog,
                    label = { Text("Cat�logo") },
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
                    selected = true,
                    onClick = { },
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
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Tarjeta principal de costo total (estilo banner naranja de referencia)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(24.dp), clip = false),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryOrange)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Costo Total Estimado",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "S/ ${"%.2f".format(totalCost)}",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 38.sp
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "soles peruanos",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Desglose por Categor�a",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryOrange)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(breakdownList) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(3.dp, RoundedCornerShape(16.dp), clip = false),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceSoft),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.category.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = TextPrimary
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = PrimaryOrangeLight
                                    ) {
                                        Text(
                                            text = "S/ ${"%.2f".format(item.totalCost)}",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = PrimaryOrangeDark,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                LinearProgressIndicator(
                                    progress = { item.percentage },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = PrimaryOrange,
                                    trackColor = InputBg
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${(item.percentage * 100).toInt()}% del presupuesto",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}