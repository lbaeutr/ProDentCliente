package dev.luisbaena.prodentclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.luisbaena.prodentclient.domain.model.WorkStatusStatistics
import dev.luisbaena.prodentclient.domain.usecase.work.GetWorkStatisticsByStatusUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla Home
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getWorkStatisticsByStatusUseCase: GetWorkStatisticsByStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    fun loadStatistics() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            val result = getWorkStatisticsByStatusUseCase()

            _uiState.value = result.fold(
                onSuccess = { statistics ->
                    HomeUiState.Success(statistics)
                },
                onFailure = { error ->
                    HomeUiState.Error(error.message ?: "Error desconocido")
                }
            )
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.RefreshStatistics -> loadStatistics()
            is HomeEvent.OnStatusCardClick -> {
            }
        }
    }
}

/**
 * Estados de la UI de Home
 */
sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(val statistics: List<WorkStatusStatistics>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

/**
 * Eventos de la pantalla Home
 */
sealed class HomeEvent {
    data object RefreshStatistics : HomeEvent()
    data class OnStatusCardClick(val statusName: String) : HomeEvent()
}

