package dev.luisbaena.prodentclient.domain.usecase.material

import dev.luisbaena.prodentclient.data.remote.dto.material.MaterialDetailDto
import dev.luisbaena.prodentclient.data.remote.dto.material.MaterialUpdateDTO
import dev.luisbaena.prodentclient.domain.repository.MaterialRepository
import javax.inject.Inject

    /**
     * UseCase para actualizar un material existente
     */
class UpdateMaterialUseCase @Inject constructor(
    private val materialRepository: MaterialRepository
) {
    suspend operator fun invoke(id: String, material: MaterialUpdateDTO): Result<MaterialDetailDto> {
        return materialRepository.updateMaterial(id, material)
    }
}