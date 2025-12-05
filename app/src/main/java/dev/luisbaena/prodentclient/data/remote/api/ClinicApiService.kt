package dev.luisbaena.prodentclient.data.remote.api

import dev.luisbaena.prodentclient.data.remote.dto.clinic.ClinicListDTO
import dev.luisbaena.prodentclient.data.remote.dto.clinic.ClinicRequestDTO
import dev.luisbaena.prodentclient.data.remote.dto.clinic.ClinicUpdateDTO
import retrofit2.Response
import retrofit2.http.*

    /**
     * API Service para gestión de clínicas
     */
interface ClinicApiService {

    /**
     * CREAR CLÍNICA
     */
    @POST("clinicas/create")
    suspend fun createClinica(
        @Header("Authorization") token: String,
        @Body clinica: ClinicRequestDTO
    ): ClinicRequestDTO

    /**
     * Lista con datos básicos de clínicas
     */
    @GET("clinicas/list")
    suspend fun getClinicasShort(
        @Header("Authorization") token: String
    ): List<ClinicListDTO>
    /**
     * OBTENER CLÍNICA POR ID
     */    @GET("clinicas/list/{id}")
    suspend fun getClinicaById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): ClinicRequestDTO

    /**
     * ACTUALIZAR CLÍNICA POR ID
     */
    @PUT("clinicas/upd/{id}")
    suspend fun updateClinica(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body clinica: ClinicUpdateDTO
    ): ClinicRequestDTO
    /**
     * ELIMINAR CLÍNICA POR ID
     * TODO: Eliminar este endpoint puesto que nosotros hacemos una desactivacion pero no una eliminacion real
     */
    @DELETE("clinicas/{id}")
    suspend fun deleteClinica(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Unit>
}