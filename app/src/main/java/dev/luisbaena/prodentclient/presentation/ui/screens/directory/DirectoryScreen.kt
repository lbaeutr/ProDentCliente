package dev.luisbaena.prodentclient.presentation.ui.screens.directory

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.luisbaena.prodentclient.presentation.ui.components.BottomNavigationBar
import dev.luisbaena.prodentclient.presentation.ui.components.Cabecera
import dev.luisbaena.prodentclient.presentation.ui.components.ListHeaderWithSort
import dev.luisbaena.prodentclient.presentation.ui.components.SearchBar
import dev.luisbaena.prodentclient.presentation.ui.components.SortOption
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.DirectoryUserCard
import dev.luisbaena.prodentclient.presentation.viewmodel.DirectoryViewModel
import dev.luisbaena.prodentclient.presentation.viewmodel.SortType

/**
 * Pantalla de Directorio de Usuarios
 * Muestra lista de usuarios con búsqueda y ordenamiento
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectoryScreen(
    modifier: Modifier = Modifier,
    viewModel: DirectoryViewModel = hiltViewModel(),
    onOpenDrawer: () -> Unit,
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsState()

    // Opciones de ordenamiento
    val sortOptions = remember {
        listOf(
            SortOption(label = "Nombre A-Z", value = "name_asc"),
            SortOption(label = "Nombre Z-A", value = "name_desc"),
            SortOption(label = "Email A-Z", value = "email_asc"),
            SortOption(label = "Email Z-A", value = "email_desc")
        )
    }

    Scaffold(
        topBar = {
            Cabecera(
                titulo = "Directorio",
                showMenuIcon = true,
                onMenuClick = onOpenDrawer
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->

        SwipeRefresh( // Todo: ver como quitar esto o que hacer con el 
            state = rememberSwipeRefreshState(uiState.isLoading),
            onRefresh = {
                viewModel.loadDirectory(forceRefresh = true)
            },
            modifier = Modifier.padding(paddingValues)
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = { viewModel.updateSearch(it) },
                    placeholder = "Buscar"
                )

                Spacer(modifier = Modifier.height(12.dp))

                ListHeaderWithSort(
                    itemCount = uiState.filteredUsers.size,
                    itemLabel = "usuario",
                    itemLabelPlural = "usuarios",
                    sortOptions = sortOptions,
                    currentSortValue = uiState.sortType.toSortValue(),
                    onSortSelected = { value ->
                        viewModel.changeSortType(value.toSortType())
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // CONTENIDO: Loading, Error o Lista
                when {
                    // Estado: Cargando (primera carga)
                    uiState.isLoading && uiState.users.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Cargando directorio...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Estado: Error
                    uiState.error != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = uiState.error!!,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { viewModel.loadDirectory(forceRefresh = true) }) {
                                    Icon(Icons.Default.Refresh, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Reintentar")
                                }
                            }
                        }
                    }

                    // Estado: Lista vacía (sin resultados)
                    uiState.filteredUsers.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SearchOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = if (uiState.searchQuery.isNotEmpty()) {
                                        "No se encontraron usuarios"
                                    } else {
                                        "No hay usuarios en el directorio"
                                    },
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (uiState.searchQuery.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TextButton(onClick = { viewModel.updateSearch("") }) {
                                        Text("Limpiar búsqueda")
                                    }
                                }
                            }
                        }
                    }

                    // Estado: Lista con usuarios
                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.filteredUsers) { user ->
                                DirectoryUserCard(user = user)
                            }
                        }
                    }
                }
            }
        }
    }
}

// FUNCIONES PARA EL COMPONENTE ListHeaderWithSort que sirven para ayudar ordenar
/**
 * Convierte SortType a String para el componente genérico
 */
private fun SortType.toSortValue(): String = when (this) {
    SortType.NAME_ASC -> "name_asc"
    SortType.NAME_DESC -> "name_desc"
    SortType.EMAIL_ASC -> "email_asc"
    SortType.EMAIL_DESC -> "email_desc"
}

/**
 * Convierte String a SortType desde el componente genérico
 */
private fun String.toSortType(): SortType = when (this) {
    "name_asc" -> SortType.NAME_ASC
    "name_desc" -> SortType.NAME_DESC
    "email_asc" -> SortType.EMAIL_ASC
    "email_desc" -> SortType.EMAIL_DESC
    else -> SortType.NAME_ASC
}