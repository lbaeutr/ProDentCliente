package dev.luisbaena.prodentclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.luisbaena.prodentclient.domain.model.Clinic
import dev.luisbaena.prodentclient.domain.usecase.clinic.CreateClinicaUseCase
import dev.luisbaena.prodentclient.domain.usecase.clinic.GetClinicaByIdUseCase
import dev.luisbaena.prodentclient.domain.usecase.clinic.GetClinicasUseCase
import dev.luisbaena.prodentclient.domain.usecase.clinic.UpdateClinicaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Tipos de ordenamiento para clínicas
 */
enum class ClinicaSortType {
    NAME_ASC,       // Nombre A-Z
    NAME_DESC,      // Nombre Z-A
    ACTIVE_FIRST,   // Activas primero
    INACTIVE_FIRST  // Inactivas primero
}

/**
 * Estado de la UI para la lista de clínicas
 */
data class ClinicasUiState(
    val clinicas: List<Clinic> = emptyList(),
    val filteredClinicas: List<Clinic> = emptyList(),
    val searchQuery: String = "",
    val sortType: ClinicaSortType = ClinicaSortType.NAME_ASC,
    val isLoading: Boolean = false,
    val error: String? = null,
    val deleteSuccess: Boolean = false,
    val clinicaToDelete: Clinic? = null
)

/**
 * Estado de la UI para crear/editar clínica
 */
data class ClinicaFormUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

/**
 * Estado del detalle de clínica
 */
data class ClinicaDetailState(
    val isLoading: Boolean = false,
    val clinica: Clinic? = null,
    val error: String? = null
)

/**
 * ViewModel para gestión de clínicas (CRUD completo)
 */
@HiltViewModel
class ClinicViewModel @Inject constructor(
    private val getClinicasUseCase: GetClinicasUseCase,
    private val getClinicaByIdUseCase: GetClinicaByIdUseCase,
    private val createClinicaUseCase: CreateClinicaUseCase,
    private val updateClinicaUseCase: UpdateClinicaUseCase,
    //private val deleteClinicaUseCase: DeleteClinicaUseCase
) : ViewModel() {

    // Estado de la lista
    private val _uiState = MutableStateFlow(ClinicasUiState())
    val uiState: StateFlow<ClinicasUiState> = _uiState.asStateFlow()

    // Estado del formulario
    private val _formState = MutableStateFlow(ClinicaFormUiState())
    val formState: StateFlow<ClinicaFormUiState> = _formState.asStateFlow()

    // Estado del detalle
    private val _detailState = MutableStateFlow(ClinicaDetailState())
    val detailState: StateFlow<ClinicaDetailState> = _detailState.asStateFlow()


    init {
        loadClinicas()
    }

    // CARGAR CLÍNICAS
    fun loadClinicas(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            val result = getClinicasUseCase(forceRefresh)

            if (result.isSuccess) {
                val clinicas = result.getOrNull() ?: emptyList()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    clinicas = clinicas,
                    filteredClinicas = applySortAndFilter(clinicas),
                    error = null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Error al cargar clínicas"
                )
            }
        }
    }


    // CARGAR DETALLE DE CLÍNICA POR ID
    fun loadClinicaById(id: String) {
        viewModelScope.launch {
            // 1. Poner en loading
            _detailState.value = ClinicaDetailState(
                isLoading = true,
                clinica = null,
                error = null
            )

            // 2. Llamar al UseCase
            val result = getClinicaByIdUseCase(id)

            // 3. Manejar resultado
            result.fold(
                onSuccess = { clinica ->
                    _detailState.value = ClinicaDetailState(
                        isLoading = false,
                        clinica = clinica,
                        error = null
                    )
                },
                onFailure = { exception ->
                    _detailState.value = ClinicaDetailState(
                        isLoading = false,
                        clinica = null,
                        error = exception.message ?: "Error al cargar clínica"
                    )
                }
            )
        }
    }

    // CREAR CLÍNICA
    fun createClinica(clinica: Clinic, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _formState.value = _formState.value.copy(
                isLoading = true,
                error = null,
                isSuccess = false
            )

            val result = createClinicaUseCase(clinica)

            if (result.isSuccess) {
                _formState.value = _formState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    error = null
                )
                //loadClinicas(forceRefresh = true) // Recargar lista
                onSuccess()
            } else {
                _formState.value = _formState.value.copy(
                    isLoading = false,
                    isSuccess = false,
                    error = result.exceptionOrNull()?.message ?: "Error al crear clínica"
                )
            }
        }
    }

    // ACTUALIZAR CLÍNICA
    fun updateClinica(id: String, clinica: Clinic, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _formState.value = _formState.value.copy(
                isLoading = true,
                error = null,
                isSuccess = false
            )

            val result = updateClinicaUseCase(id, clinica)

            if (result.isSuccess) {
                _formState.value = _formState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    error = null
                )
                loadClinicas(forceRefresh = true)
                onSuccess()
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Error al actualizar clínica"
                _formState.value = _formState.value.copy(
                    isLoading = false,
                    isSuccess = false,
                    error = errorMsg
                )
            }
        }
    }

    // BÚSQUEDA
    fun updateSearch(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFiltersAndSort()
    }

    // ORDENAMIENTO
    fun changeSortType(sortType: ClinicaSortType) {
        _uiState.value = _uiState.value.copy(sortType = sortType)
        applyFiltersAndSort()
    }


    // APLICAR FILTROS Y ORDENAMIENTO
    private fun applyFiltersAndSort() {
        val filtered = applySortAndFilter(_uiState.value.clinicas)
        _uiState.value = _uiState.value.copy(filteredClinicas = filtered)
    }

    // FUNCION QUE FILTRA Y ORDENA
    private fun applySortAndFilter(clinicas: List<Clinic>): List<Clinic> {
        val query = _uiState.value.searchQuery.trim().lowercase()

        // 1. FILTRAR por búsqueda
        val filtered = if (query.isBlank()) {
            clinicas
        } else {
            clinicas.filter { clinica ->
                clinica.nombre.lowercase().contains(query) ||
                        clinica.telefono.contains(query) ||
                        clinica.activa.toString().lowercase().contains(query)
            }
        }

        // 2. ORDENAR
        return when (_uiState.value.sortType) {
            ClinicaSortType.NAME_ASC -> filtered.sortedBy { it.nombre.lowercase() }
            ClinicaSortType.NAME_DESC -> filtered.sortedByDescending { it.nombre.lowercase() }
            ClinicaSortType.ACTIVE_FIRST -> filtered.sortedByDescending { it.activa }
            ClinicaSortType.INACTIVE_FIRST -> filtered.sortedBy { it.activa }
        }
    }

    // LIMPIAR ESTADOS
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearFormError() {
        _formState.value = _formState.value.copy(error = null)
    }

    fun clearDeleteSuccess() {
        _uiState.value = _uiState.value.copy(deleteSuccess = false)
    }

    fun resetFormState() {
        _formState.value = ClinicaFormUiState()
    }

    fun resetDetailState() {
        _detailState.value = ClinicaDetailState()
    }
}