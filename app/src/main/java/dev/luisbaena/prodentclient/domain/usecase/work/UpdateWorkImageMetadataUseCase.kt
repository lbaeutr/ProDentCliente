package dev.luisbaena.prodentclient.domain.usecase.work

import dev.luisbaena.prodentclient.data.remote.dto.work.WorkDetailDto
import dev.luisbaena.prodentclient.domain.repository.WorkRepository
import javax.inject.Inject

    /**
     * UseCase para actualizar los metadatos de una imagen de un trabajo
     */
class UpdateWorkImageMetadataUseCase @Inject constructor(
    private val workRepository: WorkRepository
) {
    suspend operator fun invoke(
        workId: String,
        imageId: String,
        imageType: String?,
        description: String?
    ): Result<WorkDetailDto> {
        return workRepository.updateImageMetadata(workId, imageId, imageType, description)
    }
}