package dev.luisbaena.prodentclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.luisbaena.prodentclient.data.remote.dto.material.MaterialDetailDto
import dev.luisbaena.prodentclient.data.remote.dto.material.MaterialListDto
import dev.luisbaena.prodentclient.data.remote.dto.material.MaterialRequestDTO
import dev.luisbaena.prodentclient.data.remote.dto.material.MaterialUpdateDTO
import dev.luisbaena.prodentclient.domain.usecase.material.CreateMaterialUseCase
import dev.luisbaena.prodentclient.domain.usecase.material.GetMaterialByIdUseCase
import dev.luisbaena.prodentclient.domain.usecase.material.GetMaterialsUseCase
import dev.luisbaena.prodentclient.domain.usecase.material.UpdateMaterialUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



/**
 * Estado del formulario (crear/editar)
 */
data class MaterialFormState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

/**
 * Estado de la lista de materiales
 */
data class MaterialsListState(
    val isLoading: Boolean = false,
    val materials: List<MaterialListDto> = emptyList(),
    val error: String? = null,
    val isEmpty: Boolean = false
)

/**
 * Estado del detalle de material
 */
data class MaterialDetailState(
    val isLoading: Boolean = false,
    val material: MaterialDetailDto? = null,
    val error: String? = null
)

/**
 * Estado de eliminación
 */
data class MaterialDeleteState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel para gestionar los materiales
 * Maneja la lógica de negocio relacionada con los materiales,
 * incluyendo la obtención de la lista, detalles, creación y actualización.
 */

@HiltViewModel
class MaterialViewModel @Inject constructor(
    private val getMaterialsUseCase: GetMaterialsUseCase,
    private val getMaterialByIdUseCase: GetMaterialByIdUseCase,
    private val createMaterialUseCase: CreateMaterialUseCase,
    private val updateMaterialUseCase: UpdateMaterialUseCase,
) : ViewModel() {


    // ESTADO DEL FORMULARIO (crear/editar)
    private val _formState = MutableStateFlow(MaterialFormState())
    val formState: StateFlow<MaterialFormState> = _formState.asStateFlow()


    // ESTADO DE LA LISTA
    private val _listState = MutableStateFlow(MaterialsListState())
    val listState: StateFlow<MaterialsListState> = _listState.asStateFlow()


    // ESTADO DEL DETALLE
    private val _detailState = MutableStateFlow(MaterialDetailState())
    val detailState: StateFlow<MaterialDetailState> = _detailState.asStateFlow()


    // ESTADO DE ELIMINACIÓN
    private val _deleteState = MutableStateFlow(MaterialDeleteState())
    val deleteState: StateFlow<MaterialDeleteState> = _deleteState.asStateFlow()


    // CARGAR LISTA DE MATERIALES
    fun loadMaterials(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _listState.value = _listState.value.copy(
                isLoading = true,
                error = null
            )

            val result = getMaterialsUseCase(forceRefresh)

            result.fold(
                onSuccess = { materials ->
                    _listState.value = MaterialsListState(
                        isLoading = false,
                        materials = materials,
                        isEmpty = materials.isEmpty(),
                        error = null
                    )
                },
                onFailure = { exception ->
                    _listState.value = MaterialsListState(
                        isLoading = false,
                        materials = emptyList(),
                        isEmpty = true,
                        error = exception.message ?: "Error al cargar materiales"
                    )
                }
            )
        }
    }


    // CARGAR MATERIAL POR ID (DETALLE)
    fun loadMaterialById(id: String) {
        viewModelScope.launch {
            _detailState.value = MaterialDetailState(isLoading = true)

            val result = getMaterialByIdUseCase(id)

            result.fold(
                onSuccess = { material ->
                    _detailState.value = MaterialDetailState(
                        isLoading = false,
                        material = material,
                        error = null
                    )
                },
                onFailure = { exception ->
                    _detailState.value = MaterialDetailState(
                        isLoading = false,
                        material = null,
                        error = exception.message ?: "Error al cargar material"
                    )
                }
            )
        }
    }


    // CREAR MATERIAL
    fun createMaterial(material: MaterialRequestDTO, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _formState.value = MaterialFormState(isLoading = true)

            val result = createMaterialUseCase(material)

            result.fold(
                onSuccess = {
                    _formState.value = MaterialFormState(
                        isLoading = false,
                        isSuccess = true
                    )
                    onSuccess()
                    loadMaterials(forceRefresh = true)
                },
                onFailure = { exception ->
                    _formState.value = MaterialFormState(
                        isLoading = false,
                        isSuccess = false,
                        error = exception.message ?: "Error al crear material"
                    )
                }
            )
        }
    }


    // ACTUALIZAR MATERIAL
    fun updateMaterial(id: String, material: MaterialUpdateDTO, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _formState.value = MaterialFormState(isLoading = true)

            val result = updateMaterialUseCase(id, material)

            result.fold(
                onSuccess = {
                    _formState.value = MaterialFormState(
                        isLoading = false,
                        isSuccess = true
                    )
                    onSuccess()
                    loadMaterials(forceRefresh = true)
                    loadMaterialById(id)
                },
                onFailure = { exception ->
                    _formState.value = MaterialFormState(
                        isLoading = false,
                        isSuccess = false,
                        error = exception.message ?: "Error al actualizar material"
                    )
                }
            )
        }
    }

    // RESETEAR ESTADO DEL FORMULARIO
    fun resetFormState() {
        _formState.value = MaterialFormState()
    }

    // RESETEAR ESTADO DEL DETALLE
    fun resetDetailState() {
        _detailState.value = MaterialDetailState()
    }

    // RESETEAR ESTADO DE ELIMINACIÓN
    fun resetDeleteState() {
        _deleteState.value = MaterialDeleteState()
    }

    // LIMPIAR ERROR DE LA LISTA
    fun clearListError() {
        _listState.value = _listState.value.copy(error = null)
    }
}