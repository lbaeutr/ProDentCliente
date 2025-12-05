package dev.luisbaena.prodentclient.presentation.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.luisbaena.prodentclient.domain.model.WorkStatusStatistics
import dev.luisbaena.prodentclient.presentation.ui.components.BottomNavigationBar
import dev.luisbaena.prodentclient.presentation.ui.components.Cabecera
import dev.luisbaena.prodentclient.presentation.ui.components.common.HomeInfoHeader
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.HomeCard
import dev.luisbaena.prodentclient.presentation.utils.groupStatisticsBySection
import dev.luisbaena.prodentclient.presentation.viewmodel.AuthViewModel
import dev.luisbaena.prodentclient.presentation.viewmodel.HomeEvent
import dev.luisbaena.prodentclient.presentation.viewmodel.HomeUiState
import dev.luisbaena.prodentclient.presentation.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    onOpenDrawer: () -> Unit
) {
    val authUiState by authViewModel.uiState.collectAsState()
    val homeUiState by homeViewModel.uiState.collectAsState()
    val user = authUiState.user ?: return

    LaunchedEffect(Unit) {
        authViewModel.refreshProfile()
        homeViewModel.loadStatistics()
    }

    Scaffold(
        topBar = {
            Cabecera(
                titulo = "Bienvenido ${user.nombre}",
                showMenuIcon = true,
                onMenuClick = onOpenDrawer,
                actions = {
                    IconButton(onClick = { homeViewModel.onEvent(HomeEvent.RefreshStatistics) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = MaterialTheme.colorScheme.onPrimary)                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        when (homeUiState) {
            is HomeUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is HomeUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = (homeUiState as HomeUiState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { homeViewModel.onEvent(HomeEvent.RefreshStatistics) }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            is HomeUiState.Success -> {
                val statistics = (homeUiState as HomeUiState.Success).statistics
                HomeContent(
                    statistics = statistics,
                    onStatusCardClick = { statusName ->
                        homeViewModel.onEvent(HomeEvent.OnStatusCardClick(statusName))
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
fun HomeContent(
    statistics: List<WorkStatusStatistics>,
    onStatusCardClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val groupedStats = groupStatisticsBySection(statistics)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Total de trabajos
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Total de Trabajos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${statistics.sumOf { it.count }}",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Secciones de estados
        groupedStats.forEach { (section, stats) ->
            item {
                HomeInfoHeader(title = section)
            }

            // Crear filas de dos tarjetas
            val rows = stats.chunked(2)
            rows.forEach { rowStats ->
                item {
                    if (rowStats.size == 1) {
                        // Si solo hay una tarjeta, mostrarla con ancho completo
                        HomeCard(
                            statistic = rowStats[0],
                           // onClick = { onStatusCardClick(rowStats[0].status.name) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        // Si hay dos tarjetas, mostrarlas en fila
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowStats.forEach { stat ->
                                HomeCard(
                                    statistic = stat,
                                   // onClick = { onStatusCardClick(stat.status.name) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}