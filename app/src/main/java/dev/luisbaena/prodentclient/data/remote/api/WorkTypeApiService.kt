package dev.luisbaena.prodentclient.data.remote.api

import dev.luisbaena.prodentclient.data.remote.dto.workType.WorkTypeDetailDto
import dev.luisbaena.prodentclient.data.remote.dto.workType.WorkTypeListDto
import dev.luisbaena.prodentclient.data.remote.dto.workType.WorkTypeRequestDTO
import dev.luisbaena.prodentclient.data.remote.dto.workType.WorkTypeUpdateDTO
import retrofit2.Response
import retrofit2.http.*

    /**
     * API Service para gestión de tipos de trabajo
     */
interface WorkTypeApiService {

    /**
     * Obtener lista de tipos de trabajo
     * GET /tipos-trabajo
     */
    @GET("tipos-trabajo")
    suspend fun getWorkTypes(
        @Header("Authorization") token: String
    ): List<WorkTypeListDto>

    /**
     * Obtener tipo de trabajo específico por ID
     * GET /tipos-trabajo/{id}
     */
    @GET("tipos-trabajo/{id}")
    suspend fun getWorkTypeById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): WorkTypeDetailDto

    /**
     * Crear nuevo tipo de trabajo
     * POST /tipos-trabajo
     */
    @POST("tipos-trabajo/create")
    suspend fun createWorkType(
        @Header("Authorization") token: String,
        @Body workType: WorkTypeRequestDTO
    ): WorkTypeDetailDto

    /**
     * Actualizar tipo de trabajo existente
     * PUT /tipos-trabajo/{id}
     */
    @PUT("tipos-trabajo/upd/{id}")
    suspend fun updateWorkType(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body workType: WorkTypeUpdateDTO
    ): WorkTypeDetailDto

    /**
     * Eliminar tipo de trabajo
     * DELETE /tipos-trabajo/{id}
     */
    @DELETE("tipos-trabajo/{id}")
    suspend fun deleteWorkType(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Unit>
}