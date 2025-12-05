package dev.luisbaena.prodentclient.domain.usecase.work

import dev.luisbaena.prodentclient.data.remote.dto.work.WorkDetailDto
import dev.luisbaena.prodentclient.domain.repository.WorkRepository
import javax.inject.Inject

    /**
     * UseCase para obtener los detalles de un trabajo por su ID
     */
class GetWorkByIdUseCase @Inject constructor(
    private val workRepository: WorkRepository
) {
    suspend operator fun invoke(id: String): Result<WorkDetailDto> {
        return workRepository.getWorkById(id)
    }
}