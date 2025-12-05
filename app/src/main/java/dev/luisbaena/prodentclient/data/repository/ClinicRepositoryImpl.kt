package dev.luisbaena.prodentclient.data.repository

import dev.luisbaena.prodentclient.data.local.preferencias.UserPreferences
import dev.luisbaena.prodentclient.data.remote.api.ClinicApiService
import dev.luisbaena.prodentclient.data.remote.dto.clinic.ClinicRequestDTO
import dev.luisbaena.prodentclient.data.remote.dto.clinic.ClinicUpdateDTO
import dev.luisbaena.prodentclient.data.remote.dto.clinic.HorariosDto
import dev.luisbaena.prodentclient.domain.model.Clinic
import dev.luisbaena.prodentclient.domain.repository.ClinicRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

    /**
     * Implementación del repository de clínicas
     */
class ClinicRepositoryImpl @Inject constructor(
    private val apiService: ClinicApiService,
    private val userPreferences: UserPreferences
) : ClinicRepository {

    override suspend fun getClinicas(forceRefresh: Boolean): Result<List<Clinic>> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val clinicList = apiService.getClinicasShort("Bearer $currentToken")

            // Mapear DTOs a modelos de dominio
            val clinics = clinicList.map { it.toDomain() }

            Result.success(clinics)

        } catch (e: HttpException) {
            // Parsear el error body para obtener el mensaje del backend
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (_: Exception) {
                null
            }

            val errorMessage = when (e.code()) {
                401 -> "Sesión expirada. Por favor, inicia sesión nuevamente"
                403 -> "No tienes permisos para ver las clínicas"
                404 -> "No se encontraron clínicas"
                500 -> "Error interno del servidor"
                502, 503 -> "Servicio no disponible temporalmente"
                else -> "Error al obtener clínicas (${e.code()})"
            }
            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun getClinicaById(id: String): Result<Clinic> {
        return try {
            // 1. Verificar que hay sesión activa
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            // 2. Llamar al API
            val response = apiService.getClinicaById("Bearer $currentToken", id)

            // 3. Mapear DTO a modelo de dominio
            Result.success(response.toDomain())

        } catch (e: HttpException) {
            // Parsear el error body para obtener el mensaje del backend
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (_: Exception) {
                null
            }

            // Mensajes de error según código HTTP
            val errorMessage = when (e.code()) {
                401 -> "Sesión expirada. Por favor, inicia sesión nuevamente"
                403 -> "No tienes permisos para ver esta clínica"
                404 -> "Clínica no encontrada"
                500 -> "Error interno del servidor"
                502, 503 -> "Servicio no disponible temporalmente"
                else -> "Error al obtener clínica (${e.code()})"
            }
            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))

        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }


    override suspend fun createClinica(clinica: Clinic): Result<Clinic> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            // Convertir modelo de dominio a DTO para enviar al backend
            val dto = ClinicRequestDTO(
                id = clinica.id,
                nombre = clinica.nombre,
                direccion = clinica.direccion,
                telefono = clinica.telefono,
                email = clinica.email,
                horarios = clinica.horarios?.let {
                    HorariosDto(
                        lunes = it.lunes,
                        martes = it.martes,
                        miercoles = it.miercoles,
                        jueves = it.jueves,
                        viernes = it.viernes
                    )
                } ?: HorariosDto(),
                activa = clinica.activa,
                fechaRegistro = clinica.fechaRegistro ?: "",
                observaciones = clinica.observaciones
            )

            val response = apiService.createClinica("Bearer $currentToken", dto)

            // Mapear respuesta a modelo de dominio
            Result.success(response.toDomain())

        } catch (e: HttpException) {
            // Parsear el error body para obtener el mensaje del backend
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (_: Exception) {
                null
            }


            val errorMessage = when (e.code()) {
                400 -> {
                    val message = errorBody ?: ""
                    when {
                        // Validación de campos obligatorios vacíos
                        message.contains("campos obligatorios están vacíos", ignoreCase = true) ||
                                message.contains("campos obligatorios estan vacios", ignoreCase = true) ->
                            "Uno o más campos obligatorios están vacíos"

                        // Validación de nombre vacío
                        message.contains("nombre", ignoreCase = true) &&
                                (message.contains("vacío", ignoreCase = true) ||
                                        message.contains("vacio", ignoreCase = true)) ->
                            "El nombre de la clínica no puede estar vacío"

                        // Validación de teléfono vacío
                        message.contains("teléfono", ignoreCase = true) &&
                                (message.contains("vacío", ignoreCase = true) ||
                                        message.contains("vacio", ignoreCase = true)) ->
                            "El teléfono de la clínica no puede estar vacío"

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
                403 -> "No tienes permisos para crear clínicas"
                409 -> {
                    val message = errorBody ?: ""
                    when {
                        // Email duplicado
                        message.contains("email", ignoreCase = true) ->
                            "Ya existe una clínica con ese email"

                        // Nombre duplicado
                        message.contains("nombre", ignoreCase = true) ->
                            "Ya existe una clínica con ese nombre"

                        // Teléfono duplicado
                        message.contains("teléfono", ignoreCase = true) ||
                                message.contains("telefono", ignoreCase = true) ->
                            "Ya existe una clínica con ese teléfono"

                        else -> {
                            // Intentar extraer el mensaje del JSON si existe
                            try {
                                val jsonPattern = """"message"\s*:\s*"([^"]+)"""".toRegex()
                                val match = jsonPattern.find(message)
                                match?.groupValues?.get(1) ?: "Ya existe una clínica con esos datos"
                            } catch (_: Exception) {
                                "Ya existe una clínica con esos datos"
                            }
                        }
                    }
                }

                422 -> "Los datos no cumplen los requisitos"
                500 -> "Error interno del servidor"
                502, 503 -> "Servicio no disponible temporalmente"
                else -> "Error al crear clínica (${e.code()})"
            }
            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))

        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }


    override suspend fun updateClinica(
        id: String,
        clinica: Clinic
    ): Result<Clinic> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            // Convertir modelo de dominio a DTO para enviar al backend
            val dto = ClinicUpdateDTO(
                nombre = clinica.nombre,
                direccion = clinica.direccion,
                telefono = clinica.telefono,
                email = clinica.email,
                horarios = clinica.horarios?.let {
                    HorariosDto(
                        lunes = it.lunes,
                        martes = it.martes,
                        miercoles = it.miercoles,
                        jueves = it.jueves,
                        viernes = it.viernes
                    )
                } ?: HorariosDto(),
                activa = clinica.activa,
                observaciones = clinica.observaciones
            )

            val response = apiService.updateClinica("Bearer $currentToken", id, dto)

            // Mapear respuesta a modelo de dominio
            Result.success(response.toDomain())

        } catch (e: HttpException) {
            // Parsear el error body para obtener el mensaje del backend
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (_: Exception) {
                null
            }

            val errorMessage = when (e.code()) {
                400 -> {
                    val message = errorBody ?: ""
                    when {
                        // Validación de campos obligatorios vacíos
                        message.contains("campos obligatorios están vacíos", ignoreCase = true) ||
                                message.contains("campos obligatorios estan vacios", ignoreCase = true) ->
                            "Uno o más campos obligatorios están vacíos"

                        // Validación de nombre vacío
                        message.contains("nombre", ignoreCase = true) &&
                                (message.contains("vacío", ignoreCase = true) ||
                                        message.contains("vacio", ignoreCase = true)) ->
                            "El nombre de la clínica no puede estar vacío"

                        // Validación de teléfono vacío
                        message.contains("teléfono", ignoreCase = true) &&
                                (message.contains("vacío", ignoreCase = true) ||
                                        message.contains("vacio", ignoreCase = true)) ->
                            "El teléfono de la clínica no puede estar vacío"

                        // Email duplicado
                        message.contains("email", ignoreCase = true) ->
                            "Ya existe una clínica con ese email"

                        // Nombre duplicado
                        message.contains("nombre", ignoreCase = true) ->
                            "Ya existe una clínica con ese nombre"

                        // Teléfono duplicado
                        message.contains("teléfono", ignoreCase = true) ||
                                message.contains("telefono", ignoreCase = true) ->
                            "Ya existe una clínica con ese teléfono"

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
                403 -> "No tienes permisos para actualizar clínicas"
                404 -> "Clínica no encontrada"
                409 -> {
                    val message = errorBody ?: ""
                    when {
                        // Email duplicado
                        message.contains("email", ignoreCase = true) ->
                            "Ya existe una clínica con ese email"

                        // Nombre duplicado
                        message.contains("nombre", ignoreCase = true) ->
                            "Ya existe una clínica con ese nombre"

                        // Teléfono duplicado
                        message.contains("teléfono", ignoreCase = true) ||
                                message.contains("telefono", ignoreCase = true) ->
                            "Ya existe una clínica con ese teléfono"

                        else -> {
                            // Intentar extraer el mensaje del JSON si existe
                            try {
                                val jsonPattern = """"message"\s*:\s*"([^"]+)"""".toRegex()
                                val match = jsonPattern.find(message)
                                match?.groupValues?.get(1) ?: "Ya existe una clínica con esos datos"
                            } catch (_: Exception) {
                                "Ya existe una clínica con esos datos"
                            }
                        }
                    }
                }

                422 -> "Los datos no cumplen los requisitos"
                500 -> "Error interno del servidor"
                502, 503 -> "Servicio no disponible temporalmente"
                else -> "Error al actualizar clínica (${e.code()})"
            }

            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))

        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }


    override suspend fun deleteClinica(id: String): Result<Unit> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            apiService.deleteClinica("Bearer $currentToken", id)
            Result.success(Unit)

        } catch (e: HttpException) {
            // Parsear el error body para obtener el mensaje del backend
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (_: Exception) {
                null
            }


            val errorMessage = when (e.code()) {
                401 -> "Sesión expirada. Por favor, inicia sesión nuevamente"
                403 -> "No tienes permisos para eliminar clínicas"
                404 -> "Clínica no encontrada"
                409 -> {
                    // Conflicto - probablemente tiene datos relacionados
                    val message = errorBody ?: ""
                    when {
                        message.contains("citas", ignoreCase = true) ||
                                message.contains("appointments", ignoreCase = true) ->
                            "No se puede eliminar. La clínica tiene citas asociadas"

                        message.contains("pacientes", ignoreCase = true) ||
                                message.contains("patients", ignoreCase = true) ->
                            "No se puede eliminar. La clínica tiene pacientes asociados"

                        else ->
                            "No se puede eliminar. La clínica tiene datos relacionados"
                    }
                }

                500 -> "Error interno del servidor"
                502, 503 -> "Servicio no disponible temporalmente"
                else -> "Error al eliminar clínica (${e.code()})"
            }
            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))

        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }
}