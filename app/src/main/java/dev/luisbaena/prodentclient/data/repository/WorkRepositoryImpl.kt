package dev.luisbaena.prodentclient.data.repository

import android.util.Log
import dev.luisbaena.prodentclient.data.local.preferencias.UserPreferences
import dev.luisbaena.prodentclient.data.remote.api.WorkApiService
import dev.luisbaena.prodentclient.data.remote.dto.work.*
import dev.luisbaena.prodentclient.domain.repository.WorkRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

    /**
     * Implementación del repositorio para la gestión de trabajos
     */
class WorkRepositoryImpl @Inject constructor(
    private val apiService: WorkApiService,
    private val userPreferences: UserPreferences
) : WorkRepository {


    // CREAR TRABAJO
    override suspend fun createWork(work: WorkRequestDTO): Result<WorkDetailDto> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val response = apiService.createWork("Bearer $currentToken", work)
            Result.success(response)

        } catch (e: HttpException) {
            handleHttpException(e, "crear trabajo, numero de trabajo ya existe")
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }


    // LISTAR Y OBTENER TRABAJOS
    override suspend fun getWorks(
        clinicaId: String?,
        dentistaId: String?,
        protesicoId: String?,
        estado: String?,
        urgente: Boolean?,
        forceRefresh: Boolean
    ): Result<List<WorkListDto>> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val response = apiService.getWorks(
                token = "Bearer $currentToken",
                clinicaId = clinicaId,
                dentistaId = dentistaId,
                protesicoId = protesicoId,
                estado = estado,
                urgente = urgente
            )
            Result.success(response)

        } catch (e: HttpException) {
            handleHttpException(e, "obtener trabajos")
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun getWorkById(id: String): Result<WorkDetailDto> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val response = apiService.getWorkById("Bearer $currentToken", id)
            Result.success(response)

        } catch (e: HttpException) {
            handleHttpException(e, "obtener trabajo")
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun getWorkByNumber(numeroTrabajo: String): Result<WorkDetailDto> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val response = apiService.getWorkByNumber("Bearer $currentToken", numeroTrabajo)
            Result.success(response)

        } catch (e: HttpException) {
            handleHttpException(e, "obtener trabajo")
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }


    // ACTUALIZAR TRABAJO
    override suspend fun updateWork(id: String, work: WorkUpdateDTO): Result<WorkDetailDto> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val response = apiService.updateWork("Bearer $currentToken", id, work)
            Result.success(response)

        } catch (e: HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (_: Exception) {
                null
            }

            Log.e("WorkRepo", "Error ${e.code()}: $errorBody")

            val errorMessage = when (e.code()) {
                400 -> "Datos inválidos. Verifica la información"
                401 -> "Sesión expirada. Por favor, inicia sesión nuevamente"
                403 -> "No tienes permisos para actualizar trabajos"
                404 -> "Trabajo no encontrado"
                422 -> "Los datos no cumplen los requisitos"
                500 -> "Error interno del servidor"
                502, 503 -> "Servicio no disponible temporalmente"
                else -> "Error al actualizar trabajo (${e.code()})"
            }
            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }


    // OPERACIONES ESPECÍFICAS
    override suspend fun changeStatus(
        id: String,
        statusChange: WorkChangeStatusDTO
    ): Result<WorkDetailDto> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val response = apiService.changeStatus("Bearer $currentToken", id, statusChange)
            Result.success(response)

        } catch (e: HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (_: Exception) {
                null
            }

            Log.e("WorkRepo", "Error ${e.code()}: $errorBody")

            val errorMessage = when (e.code()) {
                400 -> {
                    val message = errorBody ?: ""
                    when {
                        message.contains("estado", ignoreCase = true) ->
                            "Estado inválido"
                        else ->
                            "Datos inválidos. Verifica la información"
                    }
                }
                401 -> "Sesión expirada. Por favor, inicia sesión nuevamente"
                403 -> "No tienes permisos para cambiar el estado"
                404 -> "Trabajo no encontrado"
                500 -> "Error interno del servidor"
                502, 503 -> "Servicio no disponible temporalmente"
                else -> "Error al cambiar estado (${e.code()})"
            }
            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun assignProsthetist(
        id: String,
        assignment: WorkAssignProsthetistDTO
    ): Result<WorkDetailDto> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val response = apiService.assignProsthetist("Bearer $currentToken", id, assignment)
            Result.success(response)

        } catch (e: HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (_: Exception) {
                null
            }

            Log.e("WorkRepo", "Error ${e.code()}: $errorBody")

            val errorMessage = when (e.code()) {
                400 -> "Datos inválidos"
                401 -> "Sesión expirada. Por favor, inicia sesión nuevamente"
                403 -> "No tienes permisos para asignar protésicos"
                404 -> "Protesico  no encontrado"
                500 -> "Error interno del servidor"
                502, 503 -> "Servicio no disponible temporalmente"
                else -> "Error al asignar protésico (${e.code()})"
            }
            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun markAsPaid(
        id: String,
        payment: WorkDirectPaymentDTO
    ): Result<WorkDetailDto> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val response = apiService.markAsPaid("Bearer $currentToken", id, payment)
            Result.success(response)

        } catch (e: HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (_: Exception) {
                null
            }

            Log.e("WorkRepo", "Error ${e.code()}: $errorBody")

            val errorMessage = when (e.code()) {
                400 -> {
                    val message = errorBody ?: ""
                    when {
                        message.contains("factura", ignoreCase = true) ->
                            "El trabajo ya está asociado a una factura"
                        else ->
                            "Datos inválidos. Verifica la información"
                    }
                }
                401 -> "Sesión expirada. Por favor, inicia sesión nuevamente"
                403 -> "No tienes permisos para marcar trabajos como pagados"
                404 -> "Trabajo no encontrado"
                500 -> "Error interno del servidor"
                502, 503 -> "Servicio no disponible temporalmente"
                else -> "Error al marcar como pagado (${e.code()})"
            }
            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    // BÚSQUEDA GLOBAL DE TRABAJOS
    override suspend fun searchGlobal(query: String): Result<List<WorkListDto>> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val response = apiService.searchGlobal("Bearer $currentToken", query)
            Result.success(response)

        } catch (e: HttpException) {
            handleHttpException(e, "buscar trabajos")
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    // GESTIÓN DE IMÁGENES
    override suspend fun uploadImage(
        workId: String,
        imagePart: MultipartBody.Part,
        imageType: String?,
        description: String?
    ): Result<WorkDetailDto> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val imageTypeBody = imageType?.let {
                okhttp3.RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    it
                )
            }

            val descriptionBody = description?.let {
                okhttp3.RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    it
                )
            }

            val response = apiService.uploadImage(
                token = "Bearer $currentToken",
                workId = workId,
                file = imagePart,
                imageType = imageTypeBody,
                description = descriptionBody
            )

            Result.success(response)

        } catch (e: HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (_: Exception) {
                null
            }

            Log.e("WorkRepo", "Error ${e.code()}: $errorBody")

            val errorMessage = when (e.code()) {
                400 -> {
                    val message = errorBody ?: ""
                    when {
                        message.contains("tipo", ignoreCase = true) ->
                            "Tipo de imagen inválido"
                        message.contains("tamaño", ignoreCase = true) ||
                                message.contains("size", ignoreCase = true) ->
                            "Imagen demasiado grande"
                        else ->
                            "Error al subir imagen: $message"
                    }
                }
                401 -> "Sesión expirada. Por favor, inicia sesión nuevamente"
                403 -> "No tienes permisos para subir imágenes"
                404 -> "Trabajo no encontrado"
                413 -> "Imagen demasiado grande"
                500 -> "Error interno del servidor"
                502, 503 -> "Servicio no disponible temporalmente"
                else -> "Error al subir imagen (${e.code()})"
            }

            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))

        } catch (e: Exception) {
            Log.e("WorkRepo", "Error inesperado: ${e.message}")
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun downloadImage(workId: String, imageId: String): Result<ResponseBody> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val response = apiService.downloadImage("Bearer $currentToken", workId, imageId)
            Result.success(response)

        } catch (e: HttpException) {
            handleHttpException(e, "descargar imagen")
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun deleteImage(workId: String, imageId: String): Result<WorkDetailDto> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val response = apiService.deleteImage("Bearer $currentToken", workId, imageId)
            Result.success(response)

        } catch (e: HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (_: Exception) {
                null
            }

            Log.e("WorkRepo", "Error ${e.code()}: $errorBody")

            val errorMessage = when (e.code()) {
                401 -> "Sesión expirada. Por favor, inicia sesión nuevamente"
                403 -> "No tienes permisos para eliminar imágenes"
                404 -> "Imagen no encontrada"
                500 -> "Error interno del servidor"
                502, 503 -> "Servicio no disponible temporalmente"
                else -> "Error al eliminar imagen (${e.code()})"
            }
            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun updateImageMetadata(
        workId: String,
        imageId: String,
        imageType: String?,
        description: String?
    ): Result<WorkDetailDto> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val response = apiService.updateImageMetadata(
                token = "Bearer $currentToken",
                workId = workId,
                imageId = imageId,
                imageType = imageType,
                description = description
            )
            Result.success(response)

        } catch (e: HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (_: Exception) {
                null
            }

            Log.e("WorkRepo", "Error ${e.code()}: $errorBody")

            val errorMessage = when (e.code()) {
                400 -> "Tipo de imagen inválido"
                401 -> "Sesión expirada. Por favor, inicia sesión nuevamente"
                403 -> "No tienes permisos para actualizar imágenes"
                404 -> "Imagen no encontrada"
                500 -> "Error interno del servidor"
                502, 503 -> "Servicio no disponible temporalmente"
                else -> "Error al actualizar imagen (${e.code()})"
            }
            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    /*
     * Obtener estadísticas de trabajos agrupadas por estado
     */
    override suspend fun getWorkStatisticsByStatus(): Result<List<WorkStatusStatisticsDto>> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val response = apiService.getWorkStatisticsByStatus("Bearer $currentToken")
            Result.success(response)

        } catch (e: HttpException) {
            handleHttpException(e, "obtener estadísticas")
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    /*
     * Manejo centralizado de excepciones HTTP
     * para que no se repitan bloques de código similares en la capa de Work en concreto en es punto
     * con mas endpoints del proyecto y necesario para que no se repita codigo en este caso 6 veces
     */
    private fun <T> handleHttpException(e: HttpException, action: String): Result<T> {
        val errorBody = try {
            e.response()?.errorBody()?.string()
        } catch (_: Exception) {
            null
        }

        Log.e("WorkRepo", "Error ${e.code()}: $errorBody")

        val errorMessage = when (e.code()) {
            400 -> "Datos inválidos al $action"
            401 -> "Sesión expirada. Por favor, inicia sesión nuevamente"
            403 -> "No tienes permisos para $action"
            404 -> when (action) {
                "obtener trabajos" -> "No se encontraron trabajos"
                else -> "Trabajo no encontrado"
            }
            500 -> "Error interno del servidor"
            502, 503 -> "Servicio no disponible temporalmente"
            else -> "Error al $action (${e.code()})"
        }
        return Result.failure(Exception(errorMessage))
    }
}