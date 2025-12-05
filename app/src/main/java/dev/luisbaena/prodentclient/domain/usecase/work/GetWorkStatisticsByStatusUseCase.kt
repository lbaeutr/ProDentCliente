package dev.luisbaena.prodentclient.domain.usecase.work

import dev.luisbaena.prodentclient.domain.model.WorkStatus
import dev.luisbaena.prodentclient.domain.model.WorkStatusStatistics
import dev.luisbaena.prodentclient.domain.repository.WorkRepository
import javax.inject.Inject

    /**
     * UseCase para obtener estadísticas de trabajos agrupadas por estado
     */
class GetWorkStatisticsByStatusUseCase @Inject constructor(
    private val workRepository: WorkRepository
) {
    suspend operator fun invoke(): Result<List<WorkStatusStatistics>> {
        return workRepository.getWorkStatisticsByStatus().map { dtoList ->
            dtoList.mapNotNull { dto ->
                // Pasar el estado (texto) a WorkStatus usando fromValue
                // El backend manda el valor del enum (ej: ESCAYOLA, TERMINADO)
                val status = WorkStatus.fromValue(dto.estado)
                status?.let {
                    WorkStatusStatistics(
                        status = it,
                        count = dto.cantidad
                    )
                }
            }
        }
    }
}

