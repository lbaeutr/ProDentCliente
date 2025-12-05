package dev.luisbaena.prodentclient.domain.repository

import dev.luisbaena.prodentclient.data.remote.dto.material.MaterialDetailDto
import dev.luisbaena.prodentclient.data.remote.dto.material.MaterialListDto
import dev.luisbaena.prodentclient.data.remote.dto.material.MaterialRequestDTO
import dev.luisbaena.prodentclient.data.remote.dto.material.MaterialUpdateDTO

    /**
     * Repository para gestión de materiales
     */
interface MaterialRepository {
    suspend fun getMaterials(forceRefresh: Boolean = false): Result<List<MaterialListDto>>
    suspend fun getMaterialById(id: String): Result<MaterialDetailDto>
    suspend fun createMaterial(material: MaterialRequestDTO): Result<MaterialDetailDto>
    suspend fun updateMaterial(id: String, material: MaterialUpdateDTO): Result<MaterialDetailDto>
    suspend fun deleteMaterial(id: String): Result<Unit>
}