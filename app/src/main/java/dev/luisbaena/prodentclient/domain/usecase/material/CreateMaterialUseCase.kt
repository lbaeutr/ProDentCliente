package dev.luisbaena.prodentclient.domain.usecase.material

import dev.luisbaena.prodentclient.data.remote.dto.material.MaterialDetailDto
import dev.luisbaena.prodentclient.data.remote.dto.material.MaterialRequestDTO
import dev.luisbaena.prodentclient.domain.repository.MaterialRepository
import javax.inject.Inject

    /**
     * UseCase para crear un nuevo material
     */
class CreateMaterialUseCase @Inject constructor(
    private val materialRepository: MaterialRepository
) {
    suspend operator fun invoke(material: MaterialRequestDTO): Result<MaterialDetailDto> {
        return materialRepository.createMaterial(material)
    }
}