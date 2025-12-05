package dev.luisbaena.prodentclient.domain.usecase.work

import dev.luisbaena.prodentclient.data.remote.dto.work.WorkChangeStatusDTO
import dev.luisbaena.prodentclient.data.remote.dto.work.WorkDetailDto
import dev.luisbaena.prodentclient.domain.repository.WorkRepository
import javax.inject.Inject

    /**
     * UseCase para cambiar el estado de un trabajo
     */
class ChangeWorkStatusUseCase @Inject constructor(
    private val workRepository: WorkRepository
) {
    suspend operator fun invoke(id: String, statusChange: WorkChangeStatusDTO): Result<WorkDetailDto> {
        return workRepository.changeStatus(id, statusChange)
    }
}