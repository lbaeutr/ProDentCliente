package dev.luisbaena.prodentclient.presentation.ui.screens.clinic

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import dev.luisbaena.prodentclient.presentation.ui.components.Cabecera
import dev.luisbaena.prodentclient.presentation.ui.components.BottomNavigationBar
import dev.luisbaena.prodentclient.presentation.viewmodel.ClinicViewModel
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.ClinicaCard

/**
 * Pantalla de lista de clínicas
 * Muestra listas de clinicas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicScreen(
    modifier: Modifier = Modifier,
    viewModel: ClinicViewModel = hiltViewModel(),
    navController: NavHostController,
    onOpenDrawer: () -> Unit,
    isAdminMode: Boolean = false // TRUE si viene desde "Administrar Clínicas", FALSE si viene desde "Clínicas"
) {
    // Observar estado de la lista
    val uiState by viewModel.uiState.collectAsState()

    // Cargar clínicas cuando entra a la pantalla
    LaunchedEffect(Unit) {
        viewModel.loadClinicas()
    }

    Scaffold(
        topBar = {
            Cabecera(
                titulo = "Clínicas",
                showMenuIcon = true,
                onMenuClick = onOpenDrawer
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) },

        ) { paddingValues ->

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // ESTADO 1: LOADING
                uiState.isLoading && uiState.clinicas.isEmpty() -> {
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
                                text = "Cargando clínicas...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // ESTADO 2: ERROR
                uiState.error != null && uiState.clinicas.isEmpty() -> {
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
                                text = uiState.error!!,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { viewModel.loadClinicas(forceRefresh = true) },
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

                // ESTADO 3: VACÍO
                uiState.clinicas.isEmpty() -> {
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
                                imageVector = Icons.Default.Business,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No hay clínicas",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Crea tu primera clínica para comenzar",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )

                        }
                    }
                }

                // ESTADO 4: LISTA CON DATOS
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.clinicas.size) { index ->
                            val clinica = uiState.clinicas[index]
                            ClinicaCard(
                                clinica = clinica,
                                onClick = {
                                    //  Navegación simple según isAdminMode , problemas en la navegacion en separacion de pantalla
                                    val route = if (isAdminMode) {
                                        "clinic_detail_admin/${clinica.id}"
                                    } else {
                                        "clinic_detail_user/${clinica.id}"
                                    }
                                    navController.navigate(route)
                                },
                                onEditClick = {
                                    // POR SI ALGUN MOTIVO SE DESEA EDITAR DESDE LA LISTA
                                },
                                onDeleteClick = {
                                    // POR SI ALGUN MOTIVO SE DESEA ELIMINAR DESDE LA LISTA
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}