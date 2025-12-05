package dev.luisbaena.prodentclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.luisbaena.prodentclient.data.remote.dto.dentist.DentistListItemDTO
import dev.luisbaena.prodentclient.data.remote.dto.dentist.DentistCreateRequestDTO
import dev.luisbaena.prodentclient.data.remote.dto.dentist.DentistCreateResponseDTO
import dev.luisbaena.prodentclient.data.remote.dto.dentist.DentistDto
import dev.luisbaena.prodentclient.domain.usecase.dentist.CreateDentistUseCase
import dev.luisbaena.prodentclient.domain.usecase.dentist.GetDentistByIdUseCase
import dev.luisbaena.prodentclient.domain.usecase.dentist.GetDentistsUseCase
import dev.luisbaena.prodentclient.domain.usecase.dentist.UpdateDentistUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado del formulario (crear/editar)
 */
data class DentistFormState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

/**
 * Estado de la lista de dentistas
 */
data class DentistsListState(
    val isLoading: Boolean = false,
    val dentists: List<DentistListItemDTO> = emptyList(),
    val error: String? = null,
    val isEmpty: Boolean = false
)

/**
 * Estado del detalle de dentista
 */
data class DentistDetailState(
    val isLoading: Boolean = false,
    val dentist: DentistDto? = null,
    val error: String? = null
)

@HiltViewModel
class DentistViewModel @Inject constructor(
    private val createDentistUseCase: CreateDentistUseCase,
    private val getDentistsUseCase: GetDentistsUseCase,
    private val getDentistByIdUseCase: GetDentistByIdUseCase,
    private val updateDentistUseCase: UpdateDentistUseCase
) : ViewModel() {

    // ESTADO DEL FORMULARIO (crear/editar)
    private val _formState = MutableStateFlow(DentistFormState())
    val formState: StateFlow<DentistFormState> = _formState.asStateFlow()


    // ESTADO DE LA LISTA
    private val _listState = MutableStateFlow(DentistsListState())
    val listState: StateFlow<DentistsListState> = _listState.asStateFlow()


    // ESTADO DEL DETALLE
    private val _detailState = MutableStateFlow(DentistDetailState())
    val detailState: StateFlow<DentistDetailState> = _detailState.asStateFlow()


    // CREAR DENTISTA
    fun createDentist(dentist: DentistCreateRequestDTO, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _formState.value = DentistFormState(isLoading = true)


            val result = createDentistUseCase(dentist)

            result.fold(
                onSuccess = {
                    _formState.value = DentistFormState(
                        isLoading = false,
                        isSuccess = true
                    )
                    onSuccess()
                },
                onFailure = { exception ->
                    _formState.value = DentistFormState(
                        isLoading = false,
                        isSuccess = false,
                        error = exception.message ?: "Error al crear dentista"
                    )
                }
            )
        }
    }


    // ACTUALIZAR DENTISTA
    fun updateDentist(id: String, dentist: DentistDto, onSuccess: () -> Unit) {
        viewModelScope.launch {
            // 1. Poner en loading
            _formState.value = DentistFormState(isLoading = true)

            // 2. Llamar al UseCase
            val result = updateDentistUseCase(id, dentist)

            // 3. Manejar resultado
            result.fold(
                onSuccess = {
                    _formState.value = DentistFormState(
                        isLoading = false,
                        isSuccess = true
                    )
                    onSuccess()
                    // Recargar lista y detalle
                    loadDentists(forceRefresh = true)
                    loadDentistById(id)
                },
                onFailure = { exception ->
                    _formState.value = DentistFormState(
                        isLoading = false,
                        isSuccess = false,
                        error = exception.message ?: "Error al actualizar dentista"
                    )
                }
            )
        }
    }



    // CARGAR LISTA DE DENTISTAS
    fun loadDentists(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            // 1. Poner en loading
            _listState.value = _listState.value.copy(
                isLoading = true,
                error = null
            )

            // 2. Llamar al UseCase
            val result = getDentistsUseCase(forceRefresh)

            // 3. Manejar resultado
            result.fold(
                onSuccess = { dentists ->
                    _listState.value = DentistsListState(
                        isLoading = false,
                        dentists = dentists,
                        isEmpty = dentists.isEmpty(),
                        error = null
                    )
                },
                onFailure = { exception ->
                    _listState.value = DentistsListState(
                        isLoading = false,
                        dentists = emptyList(),
                        isEmpty = true,
                        error = exception.message ?: "Error al cargar dentistas"
                    )
                }
            )
        }
    }


    // CARGAR DENTISTA POR ID (DETALLE)
    fun loadDentistById(id: String) {
        viewModelScope.launch {
            // 1. Poner en loading
            _detailState.value = DentistDetailState(isLoading = true)

            // 2. Llamar al UseCase
            val result = getDentistByIdUseCase(id)

            // 3. Manejar resultado
            result.fold(
                onSuccess = { dentist ->
                    _detailState.value = DentistDetailState(
                        isLoading = false,
                        dentist = dentist,
                        error = null
                    )
                },
                onFailure = { exception ->
                    _detailState.value = DentistDetailState(
                        isLoading = false,
                        dentist = null,
                        error = exception.message ?: "Error al cargar dentista"
                    )
                }
            )
        }
    }

    // RESETEAR ESTADO DEL FORMULARIO
    fun resetFormState() {
        _formState.value = DentistFormState()
    }

    // RESETEAR ESTADO DEL DETALLE
    fun resetDetailState() {
        _detailState.value = DentistDetailState()
    }

    // LIMPIAR ERROR DE LA LISTA
    fun clearListError() {
        _listState.value = _listState.value.copy(error = null)
    }
}