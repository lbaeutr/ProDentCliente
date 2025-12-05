package dev.luisbaena.prodentclient.presentation.ui.screens.dentist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.luisbaena.prodentclient.presentation.ui.components.BottomNavigationBar
import dev.luisbaena.prodentclient.presentation.ui.components.Cabecera
import dev.luisbaena.prodentclient.presentation.ui.components.common.DentistDetailContent
import dev.luisbaena.prodentclient.presentation.viewmodel.DentistViewModel

    /**
     * Pantalla de Detalle de Dentista (ADMINISTRADOR)
     * Muestra toda la información del dentista con opciones de edición
     * Incluye botones de acción para administradores
     */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DentistDetailScreenAdmin(
    dentistId: String,
    navController: NavController,
    onOpenDrawer: () -> Unit,
    viewModel: DentistViewModel = hiltViewModel()
) {
    val detailState by viewModel.detailState.collectAsState()
    val scrollState = rememberScrollState()

    // Cargar datos del dentista al entrar
    LaunchedEffect(dentistId) {
        viewModel.loadDentistById(dentistId)
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
                titulo = "Detalle del Dentista",
                showMenuIcon = true,
                showBackIcon = true,
                onMenuClick = onOpenDrawer,
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // ESTADO 1: CARGANDO
                detailState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Cargando información...",
                                style = MaterialTheme.typography.bodyLarge
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
                                modifier = Modifier.height(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Error",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = detailState.error ?: "Error desconocido",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { viewModel.loadDentistById(dentistId) }
                            ) {
                                Icon(Icons.Default.Error, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Reintentar")
                            }
                        }
                    }
                }

                // ESTADO 3: DATOS CARGADOS
                detailState.dentist != null -> {
                    DentistDetailContent(
                        dentist = detailState.dentist!!,
                        scrollState = scrollState,
                        onEditClick = {
                            // TODO: Navegar a editar dentista
                            // navController.navigate("edit_dentist/${dentistId}")
                        },
                        showButtons = true,
                        navController = navController // ADMIN ve los botones
                    )
                }
            }
        }
    }
}