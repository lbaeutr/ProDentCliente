package dev.luisbaena.prodentclient.domain.usecase.workType

import dev.luisbaena.prodentclient.data.remote.dto.workType.WorkTypeListDto
import dev.luisbaena.prodentclient.domain.repository.WorkTypeRepository
import javax.inject.Inject

    /**
     * UseCase para obtener lista de tipos de trabajo
     */
class GetWorkTypesUseCase @Inject constructor(
    private val workTypeRepository: WorkTypeRepository
) {
    suspend operator fun invoke(forceRefresh: Boolean = false): Result<List<WorkTypeListDto>> {
        return workTypeRepository.getWorkTypes(forceRefresh)
    }
}