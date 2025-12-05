package dev.luisbaena.prodentclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.luisbaena.prodentclient.data.remote.dto.workType.WorkTypeDetailDto
import dev.luisbaena.prodentclient.data.remote.dto.workType.WorkTypeListDto
import dev.luisbaena.prodentclient.data.remote.dto.workType.WorkTypeRequestDTO
import dev.luisbaena.prodentclient.data.remote.dto.workType.WorkTypeUpdateDTO
import dev.luisbaena.prodentclient.domain.usecase.workType.CreateWorkTypeUseCase
import dev.luisbaena.prodentclient.domain.usecase.workType.GetWorkTypeByIdUseCase
import dev.luisbaena.prodentclient.domain.usecase.workType.GetWorkTypesUseCase
import dev.luisbaena.prodentclient.domain.usecase.workType.UpdateWorkTypeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Estado del formulario (crear/editar)
 */
data class WorkTypeFormState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

/**
 * Estado de la lista de tipos de trabajo
 */
data class WorkTypesListState(
    val isLoading: Boolean = false,
    val workTypes: List<WorkTypeListDto> = emptyList(),
    val error: String? = null,
    val isEmpty: Boolean = false
)

/**
 * Estado del detalle de tipo de trabajo
 */
data class WorkTypeDetailState(
    val isLoading: Boolean = false,
    val workType: WorkTypeDetailDto? = null,
    val error: String? = null
)

/**
 * Estado de eliminación
 */
data class WorkTypeDeleteState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel para gestionar los tipos de trabajo
 * Contiene la lógica para cargar, crear, actualizar y eliminar tipos de trabajo
 * y maneja los diferentes estados de la UI
 */
@HiltViewModel
class WorkTypeViewModel @Inject constructor(
    private val getWorkTypesUseCase: GetWorkTypesUseCase,
    private val getWorkTypeByIdUseCase: GetWorkTypeByIdUseCase,
    private val createWorkTypeUseCase: CreateWorkTypeUseCase,
    private val updateWorkTypeUseCase: UpdateWorkTypeUseCase,
) : ViewModel() {

    // ESTADO DEL FORMULARIO (crear/editar)
    private val _formState = MutableStateFlow(WorkTypeFormState())
    val formState: StateFlow<WorkTypeFormState> = _formState.asStateFlow()

    // ESTADO DE LA LISTA
    private val _listState = MutableStateFlow(WorkTypesListState())
    val listState: StateFlow<WorkTypesListState> = _listState.asStateFlow()

    // ESTADO DEL DETALLE
    private val _detailState = MutableStateFlow(WorkTypeDetailState())
    val detailState: StateFlow<WorkTypeDetailState> = _detailState.asStateFlow()

    // ESTADO DE ELIMINACIÓN
    private val _deleteState = MutableStateFlow(WorkTypeDeleteState())
    val deleteState: StateFlow<WorkTypeDeleteState> = _deleteState.asStateFlow()

    // CARGAR LISTA DE TIPOS DE TRABAJO
    fun loadWorkTypes(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            // 1. Poner en loading
            _listState.value = _listState.value.copy(
                isLoading = true,
                error = null
            )

            // 2. Llamar al UseCase
            val result = getWorkTypesUseCase(forceRefresh)

            // 3. Manejar resultado
            result.fold(
                onSuccess = { workTypes ->
                    _listState.value = WorkTypesListState(
                        isLoading = false,
                        workTypes = workTypes,
                        isEmpty = workTypes.isEmpty(),
                        error = null
                    )
                },
                onFailure = { exception ->
                    _listState.value = WorkTypesListState(
                        isLoading = false,
                        workTypes = emptyList(),
                        isEmpty = true,
                        error = exception.message ?: "Error al cargar tipos de trabajo"
                    )
                }
            )
        }
    }

    // CARGAR TIPO DE TRABAJO POR ID (DETALLE)
    fun loadWorkTypeById(id: String) {
        viewModelScope.launch {
            // 1. Poner en loading
            _detailState.value = WorkTypeDetailState(isLoading = true)

            // 2. Llamar al UseCase
            val result = getWorkTypeByIdUseCase(id)

            // 3. Manejar resultado
            result.fold(
                onSuccess = { workType ->
                    _detailState.value = WorkTypeDetailState(
                        isLoading = false,
                        workType = workType,
                        error = null
                    )
                },
                onFailure = { exception ->
                    _detailState.value = WorkTypeDetailState(
                        isLoading = false,
                        workType = null,
                        error = exception.message ?: "Error al cargar tipo de trabajo"
                    )
                }
            )
        }
    }


    // CREAR TIPO DE TRABAJO
    fun createWorkType(workType: WorkTypeRequestDTO, onSuccess: () -> Unit) {
        viewModelScope.launch {
            // 1. Poner en loading
            _formState.value = WorkTypeFormState(isLoading = true)

            // 2. Llamar al UseCase
            val result = createWorkTypeUseCase(workType)

            // 3. Manejar resultado
            result.fold(
                onSuccess = {
                    _formState.value = WorkTypeFormState(
                        isLoading = false,
                        isSuccess = true
                    )
                    onSuccess()
                    // Recargar lista
                    loadWorkTypes(forceRefresh = true)
                },
                onFailure = { exception ->
                    _formState.value = WorkTypeFormState(
                        isLoading = false,
                        isSuccess = false,
                        error = exception.message ?: "Error al crear tipo de trabajo"
                    )
                }
            )
        }
    }

    // ACTUALIZAR TIPO DE TRABAJO
    fun updateWorkType(id: String, workType: WorkTypeUpdateDTO, onSuccess: () -> Unit) {
        viewModelScope.launch {
            // 1. Poner en loading
            _formState.value = WorkTypeFormState(isLoading = true)

            // 2. Llamar al UseCase
            val result = updateWorkTypeUseCase(id, workType)

            // 3. Manejar resultado
            result.fold(
                onSuccess = {
                    _formState.value = WorkTypeFormState(
                        isLoading = false,
                        isSuccess = true
                    )
                    onSuccess()
                    // Recargar lista y detalle
                    loadWorkTypes(forceRefresh = true)
                    loadWorkTypeById(id)
                },
                onFailure = { exception ->
                    _formState.value = WorkTypeFormState(
                        isLoading = false,
                        isSuccess = false,
                        error = exception.message ?: "Error al actualizar tipo de trabajo"
                    )
                }
            )
        }
    }


    // RESETEAR ESTADO DEL FORMULARIO
    fun resetFormState() {
        _formState.value = WorkTypeFormState()
    }

    // RESETEAR ESTADO DEL DETALLE
    fun resetDetailState() {
        _detailState.value = WorkTypeDetailState()
    }

    // RESETEAR ESTADO DE ELIMINACIÓN
    fun resetDeleteState() {
        _deleteState.value = WorkTypeDeleteState()
    }

    // LIMPIAR ERROR DE LA LISTA

    fun clearListError() {
        _listState.value = _listState.value.copy(error = null)
    }
}