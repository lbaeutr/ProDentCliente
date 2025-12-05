package dev.luisbaena.prodentclient.domain.repository

import dev.luisbaena.prodentclient.data.remote.dto.work.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody

    /**
     * Repository for work management
     */
interface WorkRepository {

    suspend fun createWork(work: WorkRequestDTO): Result<WorkDetailDto>

    suspend fun getWorks(
        clinicaId: String? = null,
        dentistaId: String? = null,
        protesicoId: String? = null,
        estado: String? = null,
        urgente: Boolean? = null,
        forceRefresh: Boolean = false
    ): Result<List<WorkListDto>>

    suspend fun getWorkById(id: String): Result<WorkDetailDto>

    suspend fun getWorkByNumber(numeroTrabajo: String): Result<WorkDetailDto>

    suspend fun updateWork(id: String, work: WorkUpdateDTO): Result<WorkDetailDto>

    // OPERACIONES ESPECÍFICAS
    suspend fun changeStatus(id: String, statusChange: WorkChangeStatusDTO): Result<WorkDetailDto>

    suspend fun assignProsthetist(id: String, assignment: WorkAssignProsthetistDTO): Result<WorkDetailDto>

    suspend fun markAsPaid(id: String, payment: WorkDirectPaymentDTO): Result<WorkDetailDto>

    suspend fun searchGlobal(query: String): Result<List<WorkListDto>>
    // GESTIÓN DE IMÁGENES
     suspend fun uploadImage(
        workId: String,
        imagePart: MultipartBody.Part,
        imageType: String?,
        description: String?
    ): Result<WorkDetailDto>

    suspend fun downloadImage(workId: String, imageId: String): Result<ResponseBody>

    suspend fun deleteImage(workId: String, imageId: String): Result<WorkDetailDto>

    suspend fun updateImageMetadata(
        workId: String,
        imageId: String,
        imageType: String?,
        description: String?
    ): Result<WorkDetailDto>

    suspend fun getWorkStatisticsByStatus(): Result<List<WorkStatusStatisticsDto>>
}