package dev.luisbaena.prodentclient.domain.usecase.workType

import dev.luisbaena.prodentclient.data.remote.dto.workType.WorkTypeDetailDto
import dev.luisbaena.prodentclient.data.remote.dto.workType.WorkTypeRequestDTO
import dev.luisbaena.prodentclient.domain.repository.WorkTypeRepository
import javax.inject.Inject

    /**
     * UseCase para crear un nuevo tipo de trabajo
     */
class CreateWorkTypeUseCase @Inject constructor(
    private val workTypeRepository: WorkTypeRepository
) {
    suspend operator fun invoke(workType: WorkTypeRequestDTO): Result<WorkTypeDetailDto> {
        return workTypeRepository.createWorkType(workType)
    }
}