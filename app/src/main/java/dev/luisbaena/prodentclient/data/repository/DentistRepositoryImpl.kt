package dev.luisbaena.prodentclient.data.repository

import android.util.Log
import dev.luisbaena.prodentclient.data.local.preferencias.UserPreferences
import dev.luisbaena.prodentclient.data.remote.api.DentistApiService
import dev.luisbaena.prodentclient.data.remote.dto.dentist.DentistListItemDTO
import dev.luisbaena.prodentclient.data.remote.dto.dentist.DentistCreateRequestDTO
import dev.luisbaena.prodentclient.data.remote.dto.dentist.DentistCreateResponseDTO
import dev.luisbaena.prodentclient.data.remote.dto.dentist.DentistDto
import dev.luisbaena.prodentclient.domain.repository.DentistRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

    /**
     * Implementación del repository de dentistas
     */
class DentistRepositoryImpl @Inject constructor(
    private val apiService: DentistApiService,
    private val userPreferences: UserPreferences
) : DentistRepository {

    // OBTENER DENTISTAS
    override suspend fun getDentists(forceRefresh: Boolean): Result<List<DentistListItemDTO>> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val response = apiService.getDentists("Bearer $currentToken")
            Result.success(response)

        } catch (e: HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (_: Exception) {
                null
            }

            Log.e("DentistRepo", "Error ${e.code()}: $errorBody")

            val errorMessage = when (e.code()) {
                401 -> "Sesión expirada. Por favor, inicia sesión nuevamente"
                403 -> "No tienes permisos para ver los dentistas"
                404 -> "No se encontraron dentistas"
                500 -> "Error interno del servidor"
                502, 503 -> "Servicio no disponible temporalmente"
                else -> "Error al obtener dentistas (${e.code()})"
            }
            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))

        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    // OBTENER DENTISTA POR ID
    override suspend fun getDentistById(id: String): Result<DentistDto> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val response = apiService.getDentistById("Bearer $currentToken", id)
            Result.success(response)

        } catch (e: HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (_: Exception) {
                null
            }

            Log.e("DentistRepo", "Error ${e.code()}: $errorBody")

            val errorMessage = when (e.code()) {
                401 -> "Sesión expirada. Por favor, inicia sesión nuevamente"
                403 -> "No tienes permisos para ver este dentista"
                404 -> "Dentista no encontrado"
                500 -> "Error interno del servidor"
                502, 503 -> "Servicio no disponible temporalmente"
                else -> "Error al obtener dentista (${e.code()})"
            }
            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))

        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    // CREAR DENTISTA
    override suspend fun createDentist(dentist: DentistCreateRequestDTO): Result<DentistCreateResponseDTO> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val response = apiService.createDentist("Bearer $currentToken", dentist)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Respuesta exitosa pero sin datos"))
                }
            } else {
                val errorBody = response.errorBody()?.string()

                val errorMessage = when (response.code()) {
                    400 -> {
                        val message = errorBody ?: ""
                        when {
                            // Validación: clínica no existe (ResourceNotFoundException)
                            message.contains("clinica solicitada no existe", ignoreCase = true) ||
                                    message.contains("clínica solicitada no existe", ignoreCase = true) ->
                                "La clínica solicitada no existe"

                            // Validación: clínica inactiva
                            message.contains("clínica inactiva", ignoreCase = true) ||
                                    message.contains("clinica inactiva", ignoreCase = true) ->
                                "No se puede asignar un dentista a una clínica inactiva"

                            // Validación: nombre vacío
                            message.contains("nombre del dentista no puede estar vacío", ignoreCase = true) ||
                                    message.contains("nombre del dentista no puede estar vacio", ignoreCase = true) ->
                                "El nombre del dentista no puede estar vacío"

                            // Validación: apellidos vacíos
                            message.contains("apellidos del dentista no pueden estar vacíos", ignoreCase = true) ||
                                    message.contains("apellidos del dentista no pueden estar vacios", ignoreCase = true) ->
                                "Los apellidos del dentista no pueden estar vacíos"

                            // Validación: teléfono duplicado en la misma clínica
                            message.contains("Ya existe un dentista con el teléfono", ignoreCase = true) ||
                                    message.contains("Ya existe un dentista con el telefono", ignoreCase = true) ->
                                "Ya existe un dentista con ese teléfono en esta clínica"

                            else -> {
                                // Intentar extraer el mensaje del JSON si existe
                                try {
                                    val jsonPattern = """"message"\s*:\s*"([^"]+)"""".toRegex()
                                    val match = jsonPattern.find(message)
                                    match?.groupValues?.get(1) ?: "Datos inválidos. Verifica la información"
                                } catch (_: Exception) {
                                    "Datos inválidos. Verifica la información"
                                }
                            }
                        }
                    }

                    401 -> "Sesión expirada. Por favor, inicia sesión nuevamente"
                    403 -> "No tienes permisos para crear dentistas"
                    404 -> "La clínica seleccionada no existe"
                    422 -> "Los datos no cumplen los requisitos"
                    500 -> "Error interno del servidor. Contacta con el administrador."
                    502, 503 -> "Servicio no disponible temporalmente"
                    else -> "Error al crear dentista (${response.code()})"
                }
                Result.failure(Exception(errorMessage))
            }

        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))

        } catch (e: kotlinx.serialization.SerializationException) {
            Result.failure(Exception("Error al procesar la respuesta del servidor: ${e.message}"))

        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    // ACTUALIZAR DENTISTA
    override suspend fun updateDentist(
        id: String,
        dentist: DentistDto
    ): Result<DentistCreateResponseDTO> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val response = apiService.updateDentist("Bearer $currentToken", id, dentist)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Respuesta exitosa pero sin datos"))
                }
            } else {
                val errorBody = response.errorBody()?.string()

                val errorMessage = when (response.code()) {
                    400 -> {
                        val message = errorBody ?: ""
                        when {
                            // Validación: nombre vacío
                            message.contains("nombre del dentista no puede estar vacío", ignoreCase = true) ||
                                    message.contains("nombre del dentista no puede estar vacio", ignoreCase = true) ->
                                "El nombre del dentista no puede estar vacío"

                            // Validación: apellidos vacíos
                            message.contains("apellidos del dentista no pueden estar vacíos", ignoreCase = true) ||
                                    message.contains("apellidos del dentista no pueden estar vacios", ignoreCase = true) ->
                                "Los apellidos del dentista no pueden estar vacíos"

                            else -> {
                                // Intentar extraer el mensaje del JSON si existe
                                try {
                                    val jsonPattern = """"message"\s*:\s*"([^"]+)"""".toRegex()
                                    val match = jsonPattern.find(message)
                                    match?.groupValues?.get(1) ?: "Datos inválidos. Verifica la información"
                                } catch (_: Exception) {
                                    "Datos inválidos. Verifica la información"
                                }
                            }
                        }
                    }
                    401 -> "Sesión expirada. Por favor, inicia sesión nuevamente"
                    403 -> "No tienes permisos para actualizar dentistas"
                    404 -> "Dentista no encontrado"
                    422 -> "Los datos no cumplen los requisitos"
                    500 -> "Error interno del servidor. Contacta con el administrador."
                    502, 503 -> "Servicio no disponible temporalmente"
                    else -> "Error al actualizar dentista (${response.code()})"
                }
                Result.failure(Exception(errorMessage))
            }

        } catch (_: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))

        } catch (e: kotlinx.serialization.SerializationException) {
            Result.failure(Exception("Error al procesar la respuesta del servidor: ${e.message}"))

        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    // ELIMINAR DENTISTA
    override suspend fun deleteDentist(id: String): Result<Unit> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            apiService.deleteDentist("Bearer $currentToken", id)
            Result.success(Unit)

        } catch (e: HttpException) {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (_: Exception) {
                null
            }

            Log.e("DentistRepo", "Error ${e.code()}: $errorBody")

            val errorMessage = when (e.code()) {
                401 -> "Sesión expirada. Por favor, inicia sesión nuevamente"
                403 -> "No tienes permisos para eliminar dentistas"
                404 -> "Dentista no encontrado"
                409 -> "Ya existe un dentista con esos datos"
                500 -> "Error interno del servidor"
                502, 503 -> "Servicio no disponible temporalmente"
                else -> "Error al eliminar dentista (${e.code()})"
            }
            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))

        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }
}

