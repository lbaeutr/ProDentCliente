package dev.luisbaena.prodentclient.data.repository


import android.util.Log
import dev.luisbaena.prodentclient.data.local.preferencias.UserPreferences
import dev.luisbaena.prodentclient.data.remote.api.WorkTypeApiService
import dev.luisbaena.prodentclient.data.remote.dto.workType.WorkTypeDetailDto
import dev.luisbaena.prodentclient.data.remote.dto.workType.WorkTypeListDto
import dev.luisbaena.prodentclient.data.remote.dto.workType.WorkTypeRequestDTO
import dev.luisbaena.prodentclient.data.remote.dto.workType.WorkTypeUpdateDTO
import dev.luisbaena.prodentclient.domain.repository.WorkTypeRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Implementación del repository de tipos de trabajo
 */
class WorkTypeRepositoryImpl @Inject constructor(
    private val apiService: WorkTypeApiService,
    private val userPreferences: UserPreferences
) : WorkTypeRepository {


    // OBTENER TIPOS DE TRABAJO
    override suspend fun getWorkTypes(forceRefresh: Boolean): Result<List<WorkTypeListDto>> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val response = apiService.getWorkTypes("Bearer $currentToken")
            Result.success(response)

        } catch (e: HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (_: Exception) {
                null
            }

            Log.e("WorkTypeRepo", "Error ${e.code()}: $errorBody")

            val errorMessage = when (e.code()) {
                401 -> "Sesión expirada. Por favor, inicia sesión nuevamente"
                403 -> "No tienes permisos para ver los tipos de trabajo"
                404 -> "No se encontraron tipos de trabajo"
                500 -> "Error interno del servidor"
                502, 503 -> "Servicio no disponible temporalmente"
                else -> "Error al obtener tipos de trabajo (${e.code()})"
            }
            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))

        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }


    // OBTENER TIPO DE TRABAJO POR ID
    override suspend fun getWorkTypeById(id: String): Result<WorkTypeDetailDto> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val response = apiService.getWorkTypeById("Bearer $currentToken", id)
            Result.success(response)

        } catch (e: HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (_: Exception) {
                null
            }

            Log.e("WorkTypeRepo", "Error ${e.code()}: $errorBody")

            val errorMessage = when (e.code()) {
                401 -> "Sesión expirada. Por favor, inicia sesión nuevamente"
                403 -> "No tienes permisos para ver este tipo de trabajo"
                404 -> "Tipo de trabajo no encontrado"
                500 -> "Error interno del servidor"
                502, 503 -> "Servicio no disponible temporalmente"
                else -> "Error al obtener tipo de trabajo (${e.code()})"
            }
            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))

        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }


    // CREAR TIPO DE TRABAJO
    override suspend fun createWorkType(workType: WorkTypeRequestDTO): Result<WorkTypeDetailDto> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val response = apiService.createWorkType("Bearer $currentToken", workType)
            Result.success(response)

        } catch (e: HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (_: Exception) {
                null
            }

            Log.e("WorkTypeRepo", "Error ${e.code()}: $errorBody")

            val errorMessage = when (e.code()) {
                400 -> {
                    val message = errorBody ?: ""
                    when {
                        message.contains("Categoría inválida", ignoreCase = true) ->
                            "Categoría inválida. Selecciona una categoría válida"

                        message.contains("nombre", ignoreCase = true) ->
                            "Ya existe un tipo de trabajo con ese nombre"

                        else ->
                            "Datos inválidos. Verifica la información"
                    }
                }

                401 -> "Sesión expirada. Por favor, inicia sesión nuevamente"
                403 -> "No tienes permisos para crear tipos de trabajo"
                409 -> "Ya existe un tipo de trabajo con ese nombre"
                422 -> "Los datos no cumplen los requisitos"
                500 -> "Error interno del servidor"
                502, 503 -> "Servicio no disponible temporalmente"
                else -> "Error al crear tipo de trabajo (${e.code()})"
            }
            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))

        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }


    // ACTUALIZAR TIPO DE TRABAJO
    override suspend fun updateWorkType(
        id: String,
        workType: WorkTypeUpdateDTO
    ): Result<WorkTypeDetailDto> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val response = apiService.updateWorkType("Bearer $currentToken", id, workType)
            Result.success(response)

        } catch (e: HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (_: Exception) {
                null
            }

            Log.e("WorkTypeRepo", "Error ${e.code()}: $errorBody")

            val errorMessage = when (e.code()) {
                400 -> {
                    val message = errorBody ?: ""
                    when {
                        message.contains("Categoría inválida", ignoreCase = true) ->
                            "Categoría inválida. Selecciona una categoría válida"

                        message.contains("nombre", ignoreCase = true) ->
                            "Ya existe un tipo de trabajo con ese nombre"

                        else ->
                            "Datos inválidos. Verifica la información"
                    }
                }

                401 -> "Sesión expirada. Por favor, inicia sesión nuevamente"
                403 -> "No tienes permisos para actualizar tipos de trabajo"
                404 -> "Tipo de trabajo no encontrado"
                409 -> "Ya existe un tipo de trabajo con ese nombre"
                422 -> "Los datos no cumplen los requisitos"
                500 -> "Error interno del servidor"
                502, 503 -> "Servicio no disponible temporalmente"
                else -> "Error al actualizar tipo de trabajo (${e.code()})"
            }
            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))

        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    // ELIMINAR TIPO DE TRABAJO
    override suspend fun deleteWorkType(id: String): Result<Unit> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            apiService.deleteWorkType("Bearer $currentToken", id)
            Result.success(Unit)

        } catch (e: HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (_: Exception) {
                null
            }

            Log.e("WorkTypeRepo", "Error ${e.code()}: $errorBody")

            val errorMessage = when (e.code()) {
                401 -> "Sesión expirada. Por favor, inicia sesión nuevamente"
                403 -> "No tienes permisos para eliminar tipos de trabajo"
                404 -> "Tipo de trabajo no encontrado"
                409 -> "No se puede eliminar. El tipo de trabajo tiene trabajos asociados"
                500 -> "Error interno del servidor"
                502, 503 -> "Servicio no disponible temporalmente"
                else -> "Error al eliminar tipo de trabajo (${e.code()})"
            }
            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))

        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }
}