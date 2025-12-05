package dev.luisbaena.prodentclient.domain.usecase.work

import dev.luisbaena.prodentclient.data.remote.dto.work.WorkAssignProsthetistDTO
import dev.luisbaena.prodentclient.data.remote.dto.work.WorkDetailDto
import dev.luisbaena.prodentclient.domain.repository.WorkRepository
import javax.inject.Inject

    /**
     * UseCase para asignar protesista a un trabajo
     */
class AssignProsthetistUseCase @Inject constructor(
    private val workRepository: WorkRepository
) {
    suspend operator fun invoke(id: String, assignment: WorkAssignProsthetistDTO): Result<WorkDetailDto> {
        return workRepository.assignProsthetist(id, assignment)
    }
}