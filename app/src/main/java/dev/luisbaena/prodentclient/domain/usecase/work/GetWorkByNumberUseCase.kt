package dev.luisbaena.prodentclient.domain.usecase.work

import dev.luisbaena.prodentclient.data.remote.dto.work.WorkDetailDto
import dev.luisbaena.prodentclient.domain.repository.WorkRepository
import javax.inject.Inject

    /**
     * UseCase para obtener el detalle de un trabajo por su número
     */
class GetWorkByNumberUseCase @Inject constructor(
    private val workRepository: WorkRepository
) {
    suspend operator fun invoke(numeroTrabajo: String): Result<WorkDetailDto> {
        return workRepository.getWorkByNumber(numeroTrabajo)
    }
}