package dev.luisbaena.prodentclient.domain.usecase.work

import dev.luisbaena.prodentclient.data.remote.dto.work.WorkDetailDto
import dev.luisbaena.prodentclient.data.remote.dto.work.WorkUpdateDTO
import dev.luisbaena.prodentclient.domain.repository.WorkRepository
import javax.inject.Inject

    /**
     * UseCase para actualizar un trabajo
     */
class UpdateWorkUseCase @Inject constructor(
    private val workRepository: WorkRepository
) {
    suspend operator fun invoke(id: String, work: WorkUpdateDTO): Result<WorkDetailDto> {
        return workRepository.updateWork(id, work)
    }
}