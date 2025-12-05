package dev.luisbaena.prodentclient.data.remote.api

import dev.luisbaena.prodentclient.data.remote.dto.material.MaterialDetailDto
import dev.luisbaena.prodentclient.data.remote.dto.material.MaterialListDto
import dev.luisbaena.prodentclient.data.remote.dto.material.MaterialRequestDTO
import dev.luisbaena.prodentclient.data.remote.dto.material.MaterialUpdateDTO
import retrofit2.Response
import retrofit2.http.*

    /**
     * API Service para gestión de materiales
     */
interface MaterialApiService {

    /**
     * Obtener lista de materiales
     * GET /materiales
     */
    @GET("materiales")
    suspend fun getMaterials(
        @Header("Authorization") token: String
    ): List<MaterialListDto>

    /**
     * Obtener material específico por ID
     * GET /materiales/{id}
     */
    @GET("materiales/{id}")
    suspend fun getMaterialById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): MaterialDetailDto

    /**
     * Crear nuevo material
     * POST /materiales
     */
    @POST("materiales/create")
    suspend fun createMaterial(
        @Header("Authorization") token: String,
        @Body material: MaterialRequestDTO
    ): MaterialDetailDto

    /**
     * Actualizar material existente
     * PUT /materiales/{id}
     */
    @PUT("materiales/upd/{id}")
    suspend fun updateMaterial(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body material: MaterialUpdateDTO
    ): MaterialDetailDto

    /**
     * Eliminar material
     * DELETE /materiales/{id}
     */
    @DELETE("materiales/{id}")
    suspend fun deleteMaterial(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Unit>
}