package dev.luisbaena.prodentclient.domain.usecase.work

import dev.luisbaena.prodentclient.data.remote.dto.work.WorkDetailDto
import dev.luisbaena.prodentclient.data.remote.dto.work.WorkRequestDTO
import dev.luisbaena.prodentclient.domain.repository.WorkRepository
import javax.inject.Inject

    /**
     * UseCase para crear un trabajo
     */
class CreateWorkUseCase @Inject constructor(
    private val workRepository: WorkRepository
) {
    suspend operator fun invoke(work: WorkRequestDTO): Result<WorkDetailDto> {
        return workRepository.createWork(work)
    }
}