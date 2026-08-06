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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

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
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Calculadora de Costos",
                        maxLines = 1,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TiticacaDeepBlue
                ),
                actions = {
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
                    selected = false,
                    onClick = onNavigateToCatalog,
                    label = { Text("Catálogo") },
                    icon = { Icon(Icons.Default.Storefront, contentDescription = null) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TiticacaTurquoise,
                        selectedTextColor = TiticacaTurquoise,
                        indicatorColor = TiticacaSky
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToItinerary,
                    label = { Text("Itinerario") },
                    icon = { Icon(Icons.Default.Map, contentDescription = null) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TiticacaTurquoise,
                        selectedTextColor = TiticacaTurquoise,
                        indicatorColor = TiticacaSky
                    )
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    label = { Text("Calculadora") },
                    icon = { Icon(Icons.Default.Calculate, contentDescription = null) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TiticacaTurquoise,
                        selectedTextColor = TiticacaTurquoise,
                        indicatorColor = TiticacaSky
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToChatList,
                    label = { Text("Reservas / Chat") },
                    icon = { Icon(Icons.Default.Chat, contentDescription = null) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TiticacaTurquoise,
                        selectedTextColor = TiticacaTurquoise,
                        indicatorColor = TiticacaSky
                    )
                )
            }
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
                // Tarjeta principal de costo total
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(10.dp, RoundedCornerShape(24.dp), clip = false),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = TiticacaDeepBlue
                    )
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
                            tint = TiticacaGold,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Costo Total Estimado",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "S/ ${"%.2f".format(totalCost)}",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 38.sp
                            ),
                            color = TiticacaGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = " soles peruanos",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Desglose por Categoría",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TiticacaDeepBlue
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TiticacaTurquoise)
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
                                            color = TiticacaDeepBlue
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = TiticacaSky
                                        ) {
                                            Text(
                                                text = "S/ ${"%.2f".format(item.totalCost)}",
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = TiticacaTurquoise,
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
                                        color = TiticacaTurquoise,
                                        trackColor = TiticacaSky
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${(item.percentage * 100).toInt()}% del presupuesto",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}