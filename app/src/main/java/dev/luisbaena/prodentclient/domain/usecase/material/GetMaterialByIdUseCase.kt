package dev.luisbaena.prodentclient.domain.usecase.material

import dev.luisbaena.prodentclient.data.remote.dto.material.MaterialDetailDto
import dev.luisbaena.prodentclient.domain.repository.MaterialRepository
import javax.inject.Inject

    /**
     * UseCase para obtener un material por ID
     */
class GetMaterialByIdUseCase @Inject constructor(
    private val materialRepository: MaterialRepository
) {
    suspend operator fun invoke(id: String): Result<MaterialDetailDto> {
        return materialRepository.getMaterialById(id)
    }
}