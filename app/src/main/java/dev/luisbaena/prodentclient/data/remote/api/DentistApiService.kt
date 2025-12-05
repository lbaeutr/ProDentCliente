package dev.luisbaena.prodentclient.data.remote.api

import dev.luisbaena.prodentclient.data.remote.dto.dentist.DentistListItemDTO
import dev.luisbaena.prodentclient.data.remote.dto.dentist.DentistCreateRequestDTO
import dev.luisbaena.prodentclient.data.remote.dto.dentist.DentistCreateResponseDTO
import dev.luisbaena.prodentclient.data.remote.dto.dentist.DentistDto
import retrofit2.Response
import retrofit2.http.*

    /**
     * API Service para gestión de dentistas
     */
interface DentistApiService {

    /**
     * Crear nuevo dentista
     * POST /dentistas/create
     */
    @POST("dentistas/create")
    suspend fun createDentist(
        @Header("Authorization") token: String,
        @Body dentist: DentistCreateRequestDTO
    ): Response<DentistCreateResponseDTO>

    /**
     * Obtener lista de dentistas (SIMPLIFICADA)
     * GET /dentistas/list
     */
    @GET("dentistas/list")
    suspend fun getDentists(
        @Header("Authorization") token: String
    ): List<DentistListItemDTO>

    /**
     * Obtener dentista específico por ID (COMPLETO)
     * GET /dentistas/{id}
     */
    @GET("dentistas/list/{id}")
    suspend fun getDentistById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): DentistDto

    /**
     * Actualizar dentista existente
     * PUT /dentistas/{id}
     */
    @PUT("dentistas/upd/{id}")
    suspend fun updateDentist(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body dentist: DentistDto
    ): Response<DentistCreateResponseDTO>

    /**
     * Eliminar dentista
     * Esta preparado para ser usado pero no se usa en la app actualmente
     */
    @DELETE("dentistas/{id}")
    suspend fun deleteDentist(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Unit>
}