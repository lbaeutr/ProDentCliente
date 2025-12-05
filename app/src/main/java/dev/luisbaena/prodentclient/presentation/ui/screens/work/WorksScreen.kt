package dev.luisbaena.prodentclient.presentation.ui.screens.work

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import dev.luisbaena.prodentclient.data.remote.dto.work.WorkListDto
import dev.luisbaena.prodentclient.domain.model.WorkStatus
import dev.luisbaena.prodentclient.presentation.ui.components.BottomNavigationBar
import dev.luisbaena.prodentclient.presentation.ui.components.Cabecera
import dev.luisbaena.prodentclient.presentation.ui.components.SearchBar
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.WorkInfoCard
import dev.luisbaena.prodentclient.presentation.ui.components.common.dialogs.WorkFilterDialog
import dev.luisbaena.prodentclient.presentation.viewmodel.WorkViewModel

/**
 * Screen para mostrar la lista de trabajos con búsqueda y filtros
 * usa WorkViewModel para manejar la lógica de carga y estado de los trabajos
 * y usa una lazy column para mostrar los trabajos en tarjetas WorkInfoCard
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorksScreen(
    modifier: Modifier = Modifier,
    viewModel: WorkViewModel = hiltViewModel(),
    navController: NavHostController,
    onOpenDrawer: () -> Unit
) {
    val listState by viewModel.listState.collectAsState()

    // Estados de filtros y búsqueda
    var showFilterDialog by rememberSaveable { mutableStateOf(false) }
    var selectedEstado by rememberSaveable { mutableStateOf<String?>(null) }
    var showOnlyUrgente by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    // Cargar trabajos al entrar
    LaunchedEffect(Unit) {
        viewModel.loadWorks()
    }

    // Aplicar filtros cuando cambien
    LaunchedEffect(selectedEstado, showOnlyUrgente) {
        viewModel.loadWorks(
            estado = selectedEstado,
            urgente = if (showOnlyUrgente) true else null,
            forceRefresh = true
        )
    }

    // Lista filtrada con búsqueda en tiempo real
    val filteredWorks = remember(listState.works, searchQuery) {
        listState.works
            .filter { work ->
                // Si NO hay búsqueda, ocultar cancelados y finalizados
                if (searchQuery.isBlank()) {
                    work.estado != WorkStatus.CANCELLED.value && work.estado != WorkStatus.FINISHED.value
                } else {
                    // Si HAY búsqueda, mostrar todo si coincide (incluido cancelados y finalizados)
                    val query = searchQuery.lowercase()
                    work.numeroTrabajo.lowercase().contains(query) ||
                            work.pacienteNombre.lowercase().contains(query) ||
                            work.clinicaNombre.lowercase().contains(query) ||
                            work.dentistaNombre.lowercase().contains(query) ||
                            (WorkStatus.fromValue(work.estado)?.displayName?.lowercase()
                                ?.contains(query) ?: false)
                }
            }
            .sortedWith(
                compareByDescending<WorkListDto> { it.urgente }
                    .thenByDescending { it.numeroTrabajo }
            )
    }

    Scaffold(
        topBar = {
            Cabecera(
                titulo = "Trabajos",
                showMenuIcon = true,
                onMenuClick = onOpenDrawer,
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // SearchBar
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Buscar por número, paciente, clínica, dentista o estado...",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Contenido
            when {
                listState.isLoading && listState.works.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Cargando trabajos...")
                        }
                    }
                }

                listState.error != null && listState.works.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                listState.error!!,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(onClick = { viewModel.loadWorks(forceRefresh = true) }) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Reintentar")
                            }
                        }
                    }
                }

                listState.isEmpty && searchQuery.isBlank() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                Icons.Default.WorkOff,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                if (selectedEstado != null || showOnlyUrgente)
                                    "No hay trabajos con estos filtros"
                                else
                                    "No hay trabajos",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            if (selectedEstado != null || showOnlyUrgente) {
                                OutlinedButton(onClick = {
                                    selectedEstado = null
                                    showOnlyUrgente = false
                                }) {
                                    Icon(Icons.Default.ClearAll, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Limpiar Filtros")
                                }
                            } else {
                                Button(onClick = { navController.navigate("create_work") }) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Crear Trabajo")
                                }
                            }
                        }
                    }
                }

                filteredWorks.isEmpty() && searchQuery.isNotBlank() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                Icons.Default.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No se encontraron trabajos",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "No hay trabajos que coincidan con \"$searchQuery\"",
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            OutlinedButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Limpiar Búsqueda")
                            }
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredWorks) { work ->
                            WorkInfoCard(
                                work = work,
                                onClick = { navController.navigate("work_detail/${work.id}") }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }

    // Diálogo de filtros
    if (showFilterDialog) {
        WorkFilterDialog(
            currentEstado = selectedEstado,
            currentUrgente = showOnlyUrgente,
            onApplyFilters = { estado, urgente ->
                selectedEstado = estado
                showOnlyUrgente = urgente
                showFilterDialog = false
            },
            onDismiss = { showFilterDialog = false }
        )
    }
}