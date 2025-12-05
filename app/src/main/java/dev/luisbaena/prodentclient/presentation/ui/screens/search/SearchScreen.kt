package dev.luisbaena.prodentclient.presentation.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.luisbaena.prodentclient.presentation.ui.components.BottomNavigationBar
import dev.luisbaena.prodentclient.presentation.ui.components.Cabecera
import dev.luisbaena.prodentclient.presentation.ui.components.QRCodeScanner
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.ErrorCard
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.InfoCard
import dev.luisbaena.prodentclient.presentation.viewmodel.WorkNavigationEvent
import dev.luisbaena.prodentclient.presentation.viewmodel.WorkViewModel

    /**
     * Pantalla de búsqueda de trabajos mediante código QR
     * Permite escanear códigos QR para acceder directamente al detalle del trabajo
     */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    onOpenDrawer: () -> Unit,
    viewModel: WorkViewModel = hiltViewModel()
) {
    val listState by viewModel.listState.collectAsState()
    var showError by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    var isSearching by rememberSaveable { mutableStateOf(false) }

    // Escuchar eventos de navegación (cuando encuentra 1+ trabajos)
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is WorkNavigationEvent.NavigateToDetail -> {
                    navController.navigate("work_detail/${event.workId}")
                    showError = false
                    isSearching = false
                }
            }
        }
    }

    // Observar estado de la lista para detectar errores o lista vacía
    LaunchedEffect(listState) {
        if (!listState.isLoading && isSearching) {
            when {
                listState.error != null -> {
                    showError = true
                    errorMessage = listState.error ?: "Error al buscar trabajo"
                    isSearching = false
                }

                listState.works.isEmpty() -> {
                    showError = true
                    errorMessage = "Trabajo no encontrado"
                    isSearching = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            Cabecera(
                titulo = "Buscar Trabajos",
                showMenuIcon = true,
                onMenuClick = onOpenDrawer
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // INFO CARD
                InfoCard(
                    message = "Escanea el código QR de un trabajo para ver su información completa",
                    modifier = Modifier.fillMaxWidth()
                )

                // LOADING O CONTENIDO
                if (listState.isLoading) {
                    // LOADING STATE SIMPLE
                    Spacer(modifier = Modifier.height(60.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(56.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 5.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Buscando trabajo...",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                } else {
                    Spacer(modifier = Modifier.height(16.dp))

                    // TÍTULO
                    Text(
                        text = "Escanear Código QR",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )



                    Spacer(modifier = Modifier.height(48.dp))

                    // CARD PRINCIPAL CON SCANNER
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp, horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // QR SCANNER INTEGRADO
                            QRCodeScanner(
                                onQRScanned = { scannedCode ->
                                    showError = false
                                    isSearching = true
                                    viewModel.searchWorks(scannedCode.trim())
                                },
                                onError = { error ->
                                    showError = true
                                    errorMessage = "Error al escanear: $error"
                                    isSearching = false
                                },
                                modifier = Modifier.size(140.dp),
                                iconSize = Modifier.size(100.dp)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Toca el icono para escanear",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ERROR CARD
                if (showError) {
                    ErrorCard(
                        errorMessage = errorMessage,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}