package dev.luisbaena.prodentclient.presentation.ui.screens.material

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import dev.luisbaena.prodentclient.presentation.ui.components.BottomNavigationBar
import dev.luisbaena.prodentclient.presentation.ui.components.Cabecera
import dev.luisbaena.prodentclient.presentation.ui.components.common.MaterialDetailContent
import dev.luisbaena.prodentclient.presentation.viewmodel.MaterialViewModel

/**
 * Pantalla de detalle de un material
 * Muestra la información detallada de un material específico
 * tal como nombre, descripción, cantidad, etc.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialDetailScreen(
    modifier: Modifier = Modifier,
    materialId: String,
    viewModel: MaterialViewModel = hiltViewModel(),
    navController: NavHostController,
    onOpenDrawer: () -> Unit
) {
    val detailState by viewModel.detailState.collectAsState()
    val scrollState = rememberScrollState()

    // Cargar material al entrar
    LaunchedEffect(materialId) {
        viewModel.loadMaterialById(materialId)
    }

    // Limpiar al salir
    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetDetailState()
        }
    }

    Scaffold(
        topBar = {
            Cabecera(
                titulo = "Detalle Material",
                showMenuIcon = true,
                onMenuClick = onOpenDrawer,
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // ESTADO 1: LOADING
                detailState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Cargando material...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // ESTADO 2: ERROR
                detailState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = detailState.error!!,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { viewModel.loadMaterialById(materialId) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary
                                )
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Reintentar")
                            }
                        }
                    }
                }

                // ESTADO 3: DATOS CARGADOS
                detailState.material != null -> {
                    MaterialDetailContent(
                        material = detailState.material!!,
                        scrollState = scrollState,
                        navController = navController
                    )
                }
            }
        }
    }
}

