package dev.luisbaena.prodentclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.luisbaena.prodentclient.data.remote.dto.work.*
import dev.luisbaena.prodentclient.domain.usecase.work.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

/**
 * Estado del formulario (crear/editar)
 */
data class WorkFormState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

/**
 * Estado de la lista de trabajos
 */
data class WorksListState(
    val isLoading: Boolean = false,
    val works: List<WorkListDto> = emptyList(),
    val error: String? = null,
    val isEmpty: Boolean = false
)

/**
 * Estado del detalle de trabajo
 */
data class WorkDetailState(
    val isLoading: Boolean = false,
    val work: WorkDetailDto? = null,
    val error: String? = null
)

/**
 * Estado de cambio de estado
 */
data class WorkStatusChangeState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

/**
 * Estado de asignación de protésico
 */
data class WorkAssignProsthetistState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

/**
 * Estado de pago directo
 */
data class WorkPaymentState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

/**
 * Estado de subida de imagen
 */
data class WorkImageUploadState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val uploadProgress: Float = 0f
)

/**
 * Estado de eliminación de imagen
 */
data class WorkImageDeleteState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

/**
 * Eventos de navegación
 */
sealed class WorkNavigationEvent {
    data class NavigateToDetail(val workId: String) : WorkNavigationEvent()
}

    /**
     * ViewModel para la gestión de trabajos
     * Contiene lógica para crear, actualizar, cargar lista, cargar detalle,
     * cambiar estado, asignar protésico, marcar como pagado, búsqueda,
     * subir y eliminar imágenes...
     */

@HiltViewModel
class WorkViewModel @Inject constructor(
    private val createWorkUseCase: CreateWorkUseCase,
    private val getWorksUseCase: GetWorksUseCase,
    private val getWorkByIdUseCase: GetWorkByIdUseCase,
    private val getWorkByNumberUseCase: GetWorkByNumberUseCase,
    private val updateWorkUseCase: UpdateWorkUseCase,
    private val changeWorkStatusUseCase: ChangeWorkStatusUseCase,
    private val assignProsthetistUseCase: AssignProsthetistUseCase,
    private val markWorkAsPaidUseCase: MarkWorkAsPaidUseCase,
    private val searchWorkUseCase: SearchWorkUseCase,
    private val uploadWorkImageUseCase: UploadWorkImageUseCase,
    private val deleteWorkImageUseCase: DeleteWorkImageUseCase,
    private val updateWorkImageMetadataUseCase: UpdateWorkImageMetadataUseCase
) : ViewModel() {


    // ESTADO DEL FORMULARIO (crear/editar)
    private val _formState = MutableStateFlow(WorkFormState())
    val formState: StateFlow<WorkFormState> = _formState.asStateFlow()


    // ESTADO DE LA LISTA
    private val _listState = MutableStateFlow(WorksListState())
    val listState: StateFlow<WorksListState> = _listState.asStateFlow()


    // ESTADO DEL DETALLE
    private val _detailState = MutableStateFlow(WorkDetailState())
    val detailState: StateFlow<WorkDetailState> = _detailState.asStateFlow()


    // ESTADO DE CAMBIO DE ESTADO
    private val _statusChangeState = MutableStateFlow(WorkStatusChangeState())
    val statusChangeState: StateFlow<WorkStatusChangeState> = _statusChangeState.asStateFlow()


    // ESTADO DE ASIGNACIÓN DE PROTÉSICO
    private val _assignProsthetistState = MutableStateFlow(WorkAssignProsthetistState())
    val assignProsthetistState: StateFlow<WorkAssignProsthetistState> = _assignProsthetistState.asStateFlow()


    // ESTADO DE PAGO DIRECTO
    private val _paymentState = MutableStateFlow(WorkPaymentState())
    val paymentState: StateFlow<WorkPaymentState> = _paymentState.asStateFlow()


    // ESTADO DE SUBIDA DE IMAGEN
    private val _imageUploadState = MutableStateFlow(WorkImageUploadState())
    val imageUploadState: StateFlow<WorkImageUploadState> = _imageUploadState.asStateFlow()


    // ESTADO DE ELIMINACIÓN DE IMAGEN
    private val _imageDeleteState = MutableStateFlow(WorkImageDeleteState())
    val imageDeleteState: StateFlow<WorkImageDeleteState> = _imageDeleteState.asStateFlow()

    // EVENTO DE NAVEGACIÓN (para navegación directa desde búsqueda)
    private val _navigationEvent = MutableSharedFlow<WorkNavigationEvent>()
    val navigationEvent: SharedFlow<WorkNavigationEvent> = _navigationEvent.asSharedFlow()

    // QUERY DE BÚSQUEDA
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()


    // CREAR TRABAJO
    fun createWork(work: WorkRequestDTO, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _formState.value = WorkFormState(isLoading = true)

            val result = createWorkUseCase(work)

            result.fold(
                onSuccess = { workDetail ->
                    _formState.value = WorkFormState(
                        isLoading = false,
                        isSuccess = true
                    )
                    onSuccess(workDetail.id) // ⬅️ DEVOLVER EL ID
                    loadWorks(forceRefresh = true)
                },
                onFailure = { exception ->
                    _formState.value = WorkFormState(
                        isLoading = false,
                        isSuccess = false,
                        error = exception.message ?: "Error al crear trabajo"
                    )
                }
            )
        }
    }


    // CARGAR LISTA DE TRABAJOS
    fun loadWorks(
        clinicaId: String? = null,
        dentistaId: String? = null,
        protesicoId: String? = null,
        estado: String? = null,
        urgente: Boolean? = null,
        forceRefresh: Boolean = false
    ) {
        viewModelScope.launch {
            _listState.value = _listState.value.copy(
                isLoading = true,
                error = null
            )

            val result = getWorksUseCase(
                clinicaId = clinicaId,
                dentistaId = dentistaId,
                protesicoId = protesicoId,
                estado = estado,
                urgente = urgente,
                forceRefresh = forceRefresh
            )

            result.fold(
                onSuccess = { works ->
                    _listState.value = WorksListState(
                        isLoading = false,
                        works = works,
                        isEmpty = works.isEmpty(),
                        error = null
                    )
                },
                onFailure = { exception ->
                    _listState.value = WorksListState(
                        isLoading = false,
                        works = emptyList(),
                        isEmpty = true,
                        error = exception.message ?: "Error al cargar trabajos"
                    )
                }
            )
        }
    }


    // CARGAR TRABAJO POR ID (DETALLE)
    fun loadWorkById(id: String) {
        viewModelScope.launch {
            _detailState.value = WorkDetailState(isLoading = true)

            val result = getWorkByIdUseCase(id)

            result.fold(
                onSuccess = { work ->
                    _detailState.value = WorkDetailState(
                        isLoading = false,
                        work = work,
                        error = null
                    )
                },
                onFailure = { exception ->
                    _detailState.value = WorkDetailState(
                        isLoading = false,
                        work = null,
                        error = exception.message ?: "Error al cargar trabajo"
                    )
                }
            )
        }
    }


    // CARGAR TRABAJO POR NÚMERO
    fun loadWorkByNumber(numeroTrabajo: String) {
        viewModelScope.launch {
            _detailState.value = WorkDetailState(isLoading = true)

            val result = getWorkByNumberUseCase(numeroTrabajo)

            result.fold(
                onSuccess = { work ->
                    _detailState.value = WorkDetailState(
                        isLoading = false,
                        work = work,
                        error = null
                    )
                },
                onFailure = { exception ->
                    _detailState.value = WorkDetailState(
                        isLoading = false,
                        work = null,
                        error = exception.message ?: "Error al cargar trabajo"
                    )
                }
            )
        }
    }


    // ACTUALIZAR TRABAJO
    fun updateWork(id: String, work: WorkUpdateDTO, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _formState.value = WorkFormState(isLoading = true)

            val result = updateWorkUseCase(id, work)

            result.fold(
                onSuccess = {
                    _formState.value = WorkFormState(
                        isLoading = false,
                        isSuccess = true
                    )
                    onSuccess()
                    loadWorks(forceRefresh = true)
                    loadWorkById(id)
                },
                onFailure = { exception ->
                    _formState.value = WorkFormState(
                        isLoading = false,
                        isSuccess = false,
                        error = exception.message ?: "Error al actualizar trabajo"
                    )
                }
            )
        }
    }


    // CAMBIAR ESTADO
    fun changeStatus(id: String, statusChange: WorkChangeStatusDTO, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _statusChangeState.value = WorkStatusChangeState(isLoading = true)

            val result = changeWorkStatusUseCase(id, statusChange)

            result.fold(
                onSuccess = {
                    _statusChangeState.value = WorkStatusChangeState(
                        isLoading = false,
                        isSuccess = true
                    )
                    onSuccess()
                    loadWorks(forceRefresh = true)
                    loadWorkById(id)
                },
                onFailure = { exception ->
                    _statusChangeState.value = WorkStatusChangeState(
                        isLoading = false,
                        isSuccess = false,
                        error = exception.message ?: "Error al cambiar estado"
                    )
                }
            )
        }
    }


    // ASIGNAR PROTÉSICO
    fun assignProsthetist(id: String, assignment: WorkAssignProsthetistDTO, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _assignProsthetistState.value = WorkAssignProsthetistState(isLoading = true)

            val result = assignProsthetistUseCase(id, assignment)

            result.fold(
                onSuccess = {
                    _assignProsthetistState.value = WorkAssignProsthetistState(
                        isLoading = false,
                        isSuccess = true
                    )
                    onSuccess()
                    loadWorks(forceRefresh = true)
                    loadWorkById(id)
                },
                onFailure = { exception ->
                    _assignProsthetistState.value = WorkAssignProsthetistState(
                        isLoading = false,
                        isSuccess = false,
                        error = exception.message ?: "Error al asignar protésico"
                    )
                }
            )
        }
    }


    // MARCAR COMO PAGADO
    fun markAsPaid(id: String, payment: WorkDirectPaymentDTO, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _paymentState.value = WorkPaymentState(isLoading = true)

            val result = markWorkAsPaidUseCase(id, payment)

            result.fold(
                onSuccess = {
                    _paymentState.value = WorkPaymentState(
                        isLoading = false,
                        isSuccess = true
                    )
                    onSuccess()
                    loadWorks(forceRefresh = true)
                    loadWorkById(id)
                },
                onFailure = { exception ->
                    _paymentState.value = WorkPaymentState(
                        isLoading = false,
                        isSuccess = false,
                        error = exception.message ?: "Error al marcar como pagado"
                    )
                }
            )
        }
    }


    // BÚSQUEDA GLOBAL DE TRABAJOS
    fun searchWorks(query: String) {
        viewModelScope.launch {
            // Actualizar query
            _searchQuery.value = query

            // Si la búsqueda está vacía, cargar todos los trabajos
            if (query.isBlank()) {
                loadWorks(forceRefresh = true)
                return@launch
            }

            // Mostrar loading
            _listState.value = _listState.value.copy(
                isLoading = true,
                error = null
            )

            // Ejecutar búsqueda
            val result = searchWorkUseCase(query)

            result.fold(
                onSuccess = { works ->
                    // Si hay 1+ resultados, navegar directamente al primero
                    if (works.isNotEmpty()) {
                        _navigationEvent.emit(WorkNavigationEvent.NavigateToDetail(works[0].id))
                        // Resetear búsqueda después de navegar
                        _searchQuery.value = ""
                    } else {
                        // Mostrar lista vacía
                        _listState.value = WorksListState(
                            isLoading = false,
                            works = emptyList(),
                            isEmpty = true,
                            error = null
                        )
                    }
                },
                onFailure = { exception ->
                    _listState.value = WorksListState(
                        isLoading = false,
                        works = emptyList(),
                        isEmpty = true,
                        error = exception.message ?: "Error al buscar trabajos"
                    )
                }
            )
        }
    }

    // Función auxiliar para resetear búsqueda
    fun clearSearch() {
        _searchQuery.value = ""
        loadWorks(forceRefresh = true)
    }


    // SUBIR IMAGEN
    fun uploadImage(
        workId: String,
        imagePart: MultipartBody.Part,
        imageType: String?,
        description: String?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _imageUploadState.value = WorkImageUploadState(isLoading = true)

            val result = uploadWorkImageUseCase(workId, imagePart, imageType, description)

            result.fold(
                onSuccess = {
                    _imageUploadState.value = WorkImageUploadState(
                        isLoading = false,
                        isSuccess = true
                    )
                    onSuccess()
                    loadWorkById(workId)
                },
                onFailure = { exception ->
                    _imageUploadState.value = WorkImageUploadState(
                        isLoading = false,
                        isSuccess = false,
                        error = exception.message ?: "Error al subir imagen"
                    )
                }
            )
        }
    }


    // ELIMINAR IMAGEN
    fun deleteImage(workId: String, imageId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _imageDeleteState.value = WorkImageDeleteState(isLoading = true)

            val result = deleteWorkImageUseCase(workId, imageId)

            result.fold(
                onSuccess = {
                    _imageDeleteState.value = WorkImageDeleteState(
                        isLoading = false,
                        isSuccess = true
                    )
                    onSuccess()
                    loadWorkById(workId)
                },
                onFailure = { exception ->
                    _imageDeleteState.value = WorkImageDeleteState(
                        isLoading = false,
                        isSuccess = false,
                        error = exception.message ?: "Error al eliminar imagen"
                    )
                }
            )
        }
    }


    // RESETEAR ESTADOS
    fun resetFormState() {
        _formState.value = WorkFormState()
    }

    fun resetDetailState() {
        _detailState.value = WorkDetailState()
    }

    fun resetStatusChangeState() {
        _statusChangeState.value = WorkStatusChangeState()
    }

    fun resetAssignProsthetistState() {
        _assignProsthetistState.value = WorkAssignProsthetistState()
    }

    fun resetPaymentState() {
        _paymentState.value = WorkPaymentState()
    }

    fun resetImageUploadState() {
        _imageUploadState.value = WorkImageUploadState()
    }

    fun resetImageDeleteState() {
        _imageDeleteState.value = WorkImageDeleteState()
    }

    fun clearListError() {
        _listState.value = _listState.value.copy(error = null)
    }
}