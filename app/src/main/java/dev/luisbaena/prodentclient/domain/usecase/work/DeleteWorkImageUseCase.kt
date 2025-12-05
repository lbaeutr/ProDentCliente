package dev.luisbaena.prodentclient.domain.usecase.work

import dev.luisbaena.prodentclient.data.remote.dto.work.WorkDetailDto
import dev.luisbaena.prodentclient.domain.repository.WorkRepository
import javax.inject.Inject

    /**
     * UseCase para eliminar una imagen de un trabajo
     */
class DeleteWorkImageUseCase @Inject constructor(
    private val workRepository: WorkRepository
) {
    suspend operator fun invoke(workId: String, imageId: String): Result<WorkDetailDto> {
        return workRepository.deleteImage(workId, imageId)
    }
}