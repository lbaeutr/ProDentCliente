package dev.luisbaena.prodentclient.data.remote.api

import dev.luisbaena.prodentclient.data.remote.dto.work.WorkAssignProsthetistDTO
import dev.luisbaena.prodentclient.data.remote.dto.work.WorkChangeStatusDTO
import dev.luisbaena.prodentclient.data.remote.dto.work.WorkDetailDto
import dev.luisbaena.prodentclient.data.remote.dto.work.WorkDirectPaymentDTO
import dev.luisbaena.prodentclient.data.remote.dto.work.WorkListDto
import dev.luisbaena.prodentclient.data.remote.dto.work.WorkRequestDTO
import dev.luisbaena.prodentclient.data.remote.dto.work.WorkStatusStatisticsDto
import dev.luisbaena.prodentclient.data.remote.dto.work.WorkUpdateDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

    /**
     * API Service de trabajos
     * Gestión completa de trabajos incluyendo imágenes
     */
interface WorkApiService {


    // CREAR TRABAJO - OPCIÓN 1: JSON (sin imágenes)
    /**
     * Create trabajo con datos en JSON (sin imágenes)
     * POST /trabajos
     */
    @POST("trabajos")
    @Headers("Content-Type: application/json")
    suspend fun createWork(
        @Header("Authorization") token: String,
        @Body work: WorkRequestDTO
    ): WorkDetailDto


    // CREAR TRABAJO - OPCIÓN 2: MULTIPART (con imágenes)
    /**
     * Create trabajo con  imágenes mixto (multipart/form-data)
     * POST /trabajos
     *
     * NOTA: Este endpoint es complejo porque usa multipart/form-data
     * Para simplificar, usaremos la opción JSON + subir imágenes después
     */
    @Multipart
    @POST("trabajos")
    suspend fun createWorkWithImages(
        @Header("Authorization") token: String,

        // Requiere campos obligatorios
        @Part("numeroTrabajo") numeroTrabajo: RequestBody,
        @Part("clinicaId") clinicaId: RequestBody,
        @Part("dentistaId") dentistaId: RequestBody,
        @Part("tipoTrabajoId") tipoTrabajoId: RequestBody,
        @Part("pacienteNombre") pacienteNombre: RequestBody,
        @Part("piezasDentales") piezasDentales: RequestBody,
        @Part("precio") precio: RequestBody,

        // opcionales que pueden ser nulos y por tanto opcionales
        @Part("materialId") materialId: RequestBody? = null,
        @Part("color") color: RequestBody? = null,
        @Part("guiaColor") guiaColor: RequestBody? = null,
        @Part("descripcionTrabajo") descripcionTrabajo: RequestBody? = null,
        @Part("observaciones") observaciones: RequestBody? = null,
        @Part("ajustePrecio") ajustePrecio: RequestBody? = null,
        @Part("urgente") urgente: RequestBody? = null,
        @Part("fechaEntregaEstimada") fechaEntregaEstimada: RequestBody? = null,
        @Part("protesicoId") protesicoId: RequestBody? = null,

        // Imagenes (listas paralelas) que pueden ser nulas y por tanto opcionales
        @Part imagenes: List<MultipartBody.Part>? = null,
        @Part("tiposImagenes") tiposImagenes: List<RequestBody>? = null,
        @Part("descripcionesImagenes") descripcionesImagenes: List<RequestBody>? = null
    ): WorkDetailDto

    // CRUD BÁSICO DE TRABAJOS
    /**
     * Listar trabajos con filtros opcionales
     * GET /trabajos
     */
    @GET("trabajos")
    suspend fun getWorks(
        @Header("Authorization") token: String,
        @Query("clinicaId") clinicaId: String? = null,
        @Query("dentistaId") dentistaId: String? = null,
        @Query("protesicoId") protesicoId: String? = null,
        @Query("estado") estado: String? = null,
        @Query("urgente") urgente: Boolean? = null
    ): List<WorkListDto>

    /**
     * Get trabajo por ID
     * GET /trabajos/{id}
     */
    @GET("trabajos/{id}")
    suspend fun getWorkById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): WorkDetailDto

    /**
     * Get trabajo por número de trabajo
     * GET /trabajos/numero/{numeroTrabajo}
     */
    @GET("trabajos/numero/{numeroTrabajo}")
    suspend fun getWorkByNumber(
        @Header("Authorization") token: String,
        @Path("numeroTrabajo") numeroTrabajo: String
    ): WorkDetailDto

    /**
     * Update trabajo
     * PUT /trabajos/{id}
     */
    @PUT("trabajos/{id}")
    suspend fun updateWork(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body work: WorkUpdateDTO
    ): WorkDetailDto

    // OPERACIONES ESPECÍFICAS
    /**
     * Cambiar estado de trabajo
     * PATCH /trabajos/{id}/estado
     */
    @PATCH("trabajos/{id}/estado")
    suspend fun changeStatus(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body statusChange: WorkChangeStatusDTO
    ): WorkDetailDto

    /**
     * Asignar protesico a trabajo
     * PATCH /trabajos/{id}/protesico
     */
    @PATCH("trabajos/{id}/protesico")
    suspend fun assignProsthetist(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body assignment: WorkAssignProsthetistDTO
    ): WorkDetailDto

    /**
     * Marcar trabajo como pagado, automaticamente
     * * PATCH /trabajos/{id}/pago
     */
    @PATCH("trabajos/{id}/pago")
    suspend fun markAsPaid(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body payment: WorkDirectPaymentDTO
    ): WorkDetailDto

    /**
     * Búsqueda global de trabajos
     * Busca en: número, paciente, clínica, dentista, tipo de trabajo, material
     * GET /trabajos/buscar?q=xxx
     */
    @GET("trabajos/buscar")
    suspend fun searchGlobal(
        @Header("Authorization") token: String,
        @Query("q") query: String
    ): List<WorkListDto>


    // GESTIÓN DE IMÁGENES
    /**
     * Upload para incorporar una nueva imagen a un trabajo
     * POST /trabajos/{id}/imagenes
     */
    @Multipart
    @POST("trabajos/{id}/imagenes")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Path("id") workId: String,
        @Part file: MultipartBody.Part,
        @Part("tipoImagen") imageType: RequestBody? = null,
        @Part("descripcion") description: RequestBody? = null
    ): WorkDetailDto

    /**
     * Download imagen
     * GET /trabajos/{id}/imagenes/{imagenId}/download
     */
    @Streaming
    @GET("trabajos/{id}/imagenes/{imagenId}/download")
    suspend fun downloadImage(
        @Header("Authorization") token: String,
        @Path("id") workId: String,
        @Path("imagenId") imageId: String
    ): ResponseBody

    /**
     * Delete imagen
     * DELETE /trabajos/{id}/imagenes/{imagenId}
     */
    @DELETE("trabajos/{id}/imagenes/{imagenId}")
    suspend fun deleteImage(
        @Header("Authorization") token: String,
        @Path("id") workId: String,
        @Path("imagenId") imageId: String
    ): WorkDetailDto

    /**
     * Update image metadata
     * PATCH /trabajos/{id}/imagenes/{imagenId}
     */
    @PATCH("trabajos/{id}/imagenes/{imagenId}")
    suspend fun updateImageMetadata(
        @Header("Authorization") token: String,
        @Path("id") workId: String,
        @Path("imagenId") imageId: String,
        @Query("tipoImagen") imageType: String? = null,
        @Query("descripcion") description: String? = null
    ): WorkDetailDto

    /**
     * Obtener estadísticas de trabajos agrupadas por estado
     * GET /trabajos/estadisticas
     */
    @GET("trabajos/estadisticas")
    suspend fun getWorkStatisticsByStatus(
        @Header("Authorization") token: String
    ): List<WorkStatusStatisticsDto>
}