package dev.luisbaena.prodentclient.domain.usecase.workType

import dev.luisbaena.prodentclient.data.remote.dto.workType.WorkTypeDetailDto
import dev.luisbaena.prodentclient.domain.repository.WorkTypeRepository
import javax.inject.Inject

    /**
     * UseCase para obtener un tipo de trabajo por ID
     */
class GetWorkTypeByIdUseCase @Inject constructor(
    private val workTypeRepository: WorkTypeRepository
) {
    suspend operator fun invoke(id: String): Result<WorkTypeDetailDto> {
        return workTypeRepository.getWorkTypeById(id)
    }
}