package dev.luisbaena.prodentclient.domain.repository

import dev.luisbaena.prodentclient.data.remote.dto.workType.WorkTypeDetailDto
import dev.luisbaena.prodentclient.data.remote.dto.workType.WorkTypeListDto
import dev.luisbaena.prodentclient.data.remote.dto.workType.WorkTypeRequestDTO
import dev.luisbaena.prodentclient.data.remote.dto.workType.WorkTypeUpdateDTO

    /**
     * Repository para gestión de tipos de trabajo
     */
interface WorkTypeRepository {
    suspend fun getWorkTypes(forceRefresh: Boolean = false): Result<List<WorkTypeListDto>>
    suspend fun getWorkTypeById(id: String): Result<WorkTypeDetailDto>
    suspend fun createWorkType(workType: WorkTypeRequestDTO): Result<WorkTypeDetailDto>
    suspend fun updateWorkType(id: String, workType: WorkTypeUpdateDTO): Result<WorkTypeDetailDto>
    suspend fun deleteWorkType(id: String): Result<Unit>
}
