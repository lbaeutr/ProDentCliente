package dev.luisbaena.prodentclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.luisbaena.prodentclient.data.remote.dto.user.DirectoryUserDto
import dev.luisbaena.prodentclient.data.remote.dto.user.fullName
import dev.luisbaena.prodentclient.domain.usecase.user.GetDirectoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Tipos de ordenamiento disponibles
 */
enum class SortType {
    NAME_ASC,      // Nombre A-Z
    NAME_DESC,     // Nombre Z-A
    EMAIL_ASC,     // Email A-Z
    EMAIL_DESC     // Email Z-A
}

/**
 * Estado de la UI del directorio
 */
data class DirectoryUiState(
    val users: List<DirectoryUserDto> = emptyList(),           // Lista completa
    val filteredUsers: List<DirectoryUserDto> = emptyList(),   // Lista filtrada
    val searchQuery: String = "",                               // Texto de búsqueda
    val sortType: SortType = SortType.NAME_ASC,                // Tipo de ordenamiento
    val isLoading: Boolean = false,                             // Indicador de carga
    val error: String? = null                                   // Mensaje de error
)

/**
 * ViewModel del directorio de usuarios
 * Maneja la lógica de:
 * - Carga de usuarios
 * - Búsqueda/filtrado
 * - Ordenamiento
 */
@HiltViewModel
class DirectoryViewModel @Inject constructor(
    private val getDirectoryUseCase: GetDirectoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DirectoryUiState())
    val uiState: StateFlow<DirectoryUiState> = _uiState.asStateFlow()

    init {
        // Cargar directorio al iniciar
        loadDirectory()
    }

    /**
     * Cargar directorio desde la API
     * Si es true, ignora cache y fuerza actualización desde API
     */
     fun loadDirectory(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            val result = getDirectoryUseCase(forceRefresh)  // ⬅️ PASAR EL PARÁMETRO

            if (result.isSuccess) {
                val users = result.getOrNull() ?: emptyList()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    users = users,
                    filteredUsers = applySortAndFilter(users),
                    error = null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Error al cargar directorio"
                )
            }
        }
    }

    /**
     * Actualizar texto de búsqueda
     */
    fun updateSearch(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFiltersAndSort()
    }

    /**
     * Cambiar tipo de ordenamiento
     */
    fun changeSortType(sortType: SortType) {
        _uiState.value = _uiState.value.copy(sortType = sortType)
        applyFiltersAndSort()
    }

    /**
     * Aplicar filtros y ordenamiento a la lista actual
     */
    private fun applyFiltersAndSort() {
        val filtered = applySortAndFilter(_uiState.value.users)
        _uiState.value = _uiState.value.copy(filteredUsers = filtered)
    }

    /**
     * Aplicar búsqueda, filtrado y ordenamiento
     */
    private fun applySortAndFilter(users: List<DirectoryUserDto>): List<DirectoryUserDto> {
        val query = _uiState.value.searchQuery.trim().lowercase()

        // 1. FILTRAR por búsqueda
        val filtered = if (query.isBlank()) {
            users
        } else {
            users.filter { user ->
                user.name.lowercase().contains(query) ||
                        user.lastname.lowercase().contains(query) ||
                        user.email.lowercase().contains(query) ||
                        user.phone.contains(query)
            }
        }

        // 2. ORDENAR según el tipo seleccionado
        return when (_uiState.value.sortType) {
            SortType.NAME_ASC -> filtered.sortedBy { it.fullName().lowercase() }
            SortType.NAME_DESC -> filtered.sortedByDescending { it.fullName().lowercase() }
            SortType.EMAIL_ASC -> filtered.sortedBy { it.email.lowercase() }
            SortType.EMAIL_DESC -> filtered.sortedByDescending { it.email.lowercase() }
        }
    }

    /**
     * Limpiar error
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}