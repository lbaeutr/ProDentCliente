package dev.luisbaena.prodentclient.domain.usecase.work

import dev.luisbaena.prodentclient.data.remote.dto.work.WorkDetailDto
import dev.luisbaena.prodentclient.domain.repository.WorkRepository
import okhttp3.MultipartBody
import javax.inject.Inject

    /**
     * UseCase actualizar imagen de un trabajo
     */
class UploadWorkImageUseCase @Inject constructor(
    private val workRepository: WorkRepository
) {
    suspend operator fun invoke(
        workId: String,
        imagePart: MultipartBody.Part,
        imageType: String?,
        description: String?
    ): Result<WorkDetailDto> {
        return workRepository.uploadImage(workId, imagePart, imageType, description)
    }
}