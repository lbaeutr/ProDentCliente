package dev.luisbaena.prodentclient.data.repository

import android.util.Log
import dev.luisbaena.prodentclient.data.local.preferencias.UserPreferences
import dev.luisbaena.prodentclient.data.remote.api.MaterialApiService
import dev.luisbaena.prodentclient.data.remote.dto.material.MaterialDetailDto
import dev.luisbaena.prodentclient.data.remote.dto.material.MaterialListDto
import dev.luisbaena.prodentclient.data.remote.dto.material.MaterialRequestDTO
import dev.luisbaena.prodentclient.data.remote.dto.material.MaterialUpdateDTO
import dev.luisbaena.prodentclient.domain.repository.MaterialRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

    /**
     * Implementación del repository de materiales
     */
class MaterialRepositoryImpl @Inject constructor(
    private val apiService: MaterialApiService,
    private val userPreferences: UserPreferences
) : MaterialRepository {

    override suspend fun getMaterials(forceRefresh: Boolean): Result<List<MaterialListDto>> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val response = apiService.getMaterials("Bearer $currentToken")
            Result.success(response)

        } catch (e: HttpException) {
            handleHttpException(e, "obtener materiales")
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun getMaterialById(id: String): Result<MaterialDetailDto> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val response = apiService.getMaterialById("Bearer $currentToken", id)
            Result.success(response)

        } catch (e: HttpException) {
            handleHttpException(e, "obtener material")
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun createMaterial(material: MaterialRequestDTO): Result<MaterialDetailDto> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val response = apiService.createMaterial("Bearer $currentToken", material)
            Result.success(response)

        } catch (e: HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (_: Exception) {
                null
            }

            Log.e("MaterialRepo", "Error ${e.code()}: $errorBody")

            val errorMessage = when (e.code()) {
                400 -> {
                    val message = errorBody ?: ""
                    when {
                        message.contains("nombre", ignoreCase = true) ->
                            "Ya existe un material con ese nombre"
                        else ->
                            "Datos inválidos. Verifica la información"
                    }
                }
                401 -> "Sesión expirada. Por favor, inicia sesión nuevamente"
                403 -> "No tienes permisos para crear materiales"
                409 -> "Ya existe un material con ese nombre"
                422 -> "Los datos no cumplen los requisitos"
                500 -> "Error interno del servidor"
                502, 503 -> "Servicio no disponible temporalmente"
                else -> "Error al crear material (${e.code()})"
            }
            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun updateMaterial(id: String, material: MaterialUpdateDTO): Result<MaterialDetailDto> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val response = apiService.updateMaterial("Bearer $currentToken", id, material)
            Result.success(response)

        } catch (e: HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (_: Exception) {
                null
            }

            Log.e("MaterialRepo", "Error ${e.code()}: $errorBody")

            val errorMessage = when (e.code()) {
                400 -> {
                    val message = errorBody ?: ""
                    when {
                        message.contains("nombre", ignoreCase = true) ->
                            "Ya existe un material con ese nombre"
                        else ->
                            "Datos inválidos. Verifica la información"
                    }
                }
                401 -> "Sesión expirada. Por favor, inicia sesión nuevamente"
                403 -> "No tienes permisos para actualizar materiales"
                404 -> "Material no encontrado"
                409 -> "Ya existe un material con ese nombre"
                422 -> "Los datos no cumplen los requisitos"
                500 -> "Error interno del servidor"
                502, 503 -> "Servicio no disponible temporalmente"
                else -> "Error al actualizar material (${e.code()})"
            }
            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun deleteMaterial(id: String): Result<Unit> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            apiService.deleteMaterial("Bearer $currentToken", id)
            Result.success(Unit)

        } catch (e: HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (_: Exception) {
                null
            }

            Log.e("MaterialRepo", "Error ${e.code()}: $errorBody")

            val errorMessage = when (e.code()) {
                401 -> "Sesión expirada. Por favor, inicia sesión nuevamente"
                403 -> "No tienes permisos para eliminar materiales"
                404 -> "Material no encontrado"
                409 -> "No se puede eliminar. El material tiene trabajos asociados"
                500 -> "Error interno del servidor"
                502, 503 -> "Servicio no disponible temporalmente"
                else -> "Error al eliminar material (${e.code()})"
            }
            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

        // Maneja excepciones HTTP y mapea códigos de error a mensajes amigables
    private fun <T> handleHttpException(e: HttpException, action: String): Result<T> {
        val errorBody = try {
            e.response()?.errorBody()?.string()
        } catch (_: Exception) {
            null
        }

        Log.e("MaterialRepo", "Error ${e.code()}: $errorBody")

        val errorMessage = when (e.code()) {
            401 -> "Sesión expirada. Por favor, inicia sesión nuevamente"
            403 -> "No tienes permisos para $action"
            404 -> when (action) {
                "obtener materiales" -> "No se encontraron materiales"
                else -> "Material no encontrado"
            }
            500 -> "Error interno del servidor"
            502, 503 -> "Servicio no disponible temporalmente"
            else -> "Error al $action (${e.code()})"
        }
        return Result.failure(Exception(errorMessage))
    }
}