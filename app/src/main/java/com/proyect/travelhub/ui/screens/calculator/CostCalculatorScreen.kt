package com.proyect.travelhub.ui.screens.calculator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

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
                title = { Text("Calculadora de Costos", maxLines = 1) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Mi Perfil")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(selected = false, onClick = onNavigateToCatalog, label = { Text("Catálogo") }, icon = { Icon(Icons.Default.Storefront, contentDescription = null) })
                NavigationBarItem(selected = false, onClick = onNavigateToItinerary, label = { Text("Itinerario") }, icon = { Icon(Icons.Default.Map, contentDescription = null) })
                NavigationBarItem(selected = true, onClick = { }, label = { Text("Calculadora") }, icon = { Icon(Icons.Default.Calculate, contentDescription = null) })
                NavigationBarItem(selected = false, onClick = onNavigateToChatList, label = { Text("Reservas / Chat") }, icon = { Icon(Icons.Default.Chat, contentDescription = null) })
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Costo Total Estimado del Viaje", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "S/ ${"%.2f".format(totalCost)}",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Desglose por Categoría:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(breakdownList) { item ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = item.category.name, style = MaterialTheme.typography.titleMedium)
                                    Text(text = "S/ ${"%.2f".format(item.totalCost)}", style = MaterialTheme.typography.titleSmall)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { item.percentage },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}