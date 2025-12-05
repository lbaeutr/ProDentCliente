package dev.luisbaena.prodentclient.domain.usecase.workType

import dev.luisbaena.prodentclient.data.remote.dto.workType.WorkTypeDetailDto
import dev.luisbaena.prodentclient.data.remote.dto.workType.WorkTypeUpdateDTO
import dev.luisbaena.prodentclient.domain.repository.WorkTypeRepository
import javax.inject.Inject

    /**
     * UseCase para actualizar un tipo de trabajo existente
     */
class UpdateWorkTypeUseCase @Inject constructor(
    private val workTypeRepository: WorkTypeRepository
) {
    suspend operator fun invoke(id: String, workType: WorkTypeUpdateDTO): Result<WorkTypeDetailDto> {
        return workTypeRepository.updateWorkType(id, workType)
    }
}