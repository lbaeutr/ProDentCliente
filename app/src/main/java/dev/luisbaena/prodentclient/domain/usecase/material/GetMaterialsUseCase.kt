package dev.luisbaena.prodentclient.domain.usecase.material

import dev.luisbaena.prodentclient.data.remote.dto.material.MaterialListDto
import dev.luisbaena.prodentclient.domain.repository.MaterialRepository
import javax.inject.Inject

    /**
     * UseCase para obtener lista de materiales
     */
class GetMaterialsUseCase @Inject constructor(
    private val materialRepository: MaterialRepository
) {
    suspend operator fun invoke(forceRefresh: Boolean = false): Result<List<MaterialListDto>> {
        return materialRepository.getMaterials(forceRefresh)
    }
}