package dev.luisbaena.prodentclient.presentation.ui.screens.clinic

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
import dev.luisbaena.prodentclient.presentation.ui.components.common.ClinicaDetailContent
import dev.luisbaena.prodentclient.presentation.viewmodel.ClinicViewModel

/**
 * Pantalla de detalle de clínica para usuarios
 * Muestra la información detallada de una clínica específica
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicaDetailScreenUser(
    clinicaId: String,
    modifier: Modifier = Modifier,
    viewModel: ClinicViewModel = hiltViewModel(),
    navController: NavHostController,
    onOpenDrawer: () -> Unit
) {
    val detailState by viewModel.detailState.collectAsState()
    val scrollState = rememberScrollState()

    // Cargar clínica al entrar
    LaunchedEffect(clinicaId) {
        viewModel.loadClinicaById(clinicaId)
    }

    Scaffold(
        topBar = {
            Cabecera(
                titulo = "Detalle de Clínica",
                showMenuIcon = true,
                onMenuClick = onOpenDrawer
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
                // LOADING
                detailState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Cargando clínica...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                // ERROR
                detailState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
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
                                onClick = { viewModel.loadClinicaById(clinicaId) },
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
                // DETALLE DE CLÍNICA
                detailState.clinica != null -> {
                    ClinicaDetailContent(
                        clinica = detailState.clinica!!,
                        scrollState = scrollState,
                        showButtons = false
                    )
                }
            }
        }
    }
}