package dev.luisbaena.prodentclient.data.repository

import android.util.Log
import dev.luisbaena.prodentclient.data.local.preferencias.DirectoryPreferences
import dev.luisbaena.prodentclient.data.local.preferencias.UserPreferences
import dev.luisbaena.prodentclient.data.remote.api.AuthApiService
import dev.luisbaena.prodentclient.data.remote.dto.user.ChangePasswordRequestDto
import dev.luisbaena.prodentclient.data.remote.dto.user.DirectoryUserDto
import dev.luisbaena.prodentclient.data.remote.dto.user.LoginRequestDto
import dev.luisbaena.prodentclient.data.remote.dto.user.RegisterRequestDto
import dev.luisbaena.prodentclient.data.remote.dto.user.UpdateProfileRequestDto
import dev.luisbaena.prodentclient.domain.model.User
import dev.luisbaena.prodentclient.domain.repository.AuthRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


    /**
     *  Logica de autenticacion conectadndose al API remoto y gestionando la sesion localmente.
     *  Implementa las operaciones definidas en AuthRepository.
     *  Login: llama al API, guarda la sesion localmente.
     *  Register: llama al API (requiere token de admin), no guarda sesion pq no loguea.
     *  Logout : llama al API (ignora errores), limpia la sesion local.(Aunque falle el API)
     *  getCurrentUser: obtiene el usuario logueado de las preferencias.
     *  saveUserSession: guarda el usuario manualmente en las preferencias.
     *  clearUserSession: limpia la sesion manualmente.
     *  isUserLoggedIn: verifica si hay un usuario logueado.
     */

    class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val userPreferences: UserPreferences,
    private val directoryPreferences: DirectoryPreferences
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val request = LoginRequestDto(email, password)
            val loginResponse = apiService.login(request)

            if (loginResponse.token.isNotEmpty()) {
                // Obtener datos completos del usuario
                try {
                    val userDto = apiService.getProfile("Bearer ${loginResponse.token}")

                    val user = User(
                        id = "",
                        nombre = userDto.name,
                        apellido = userDto.lastname,
                        email = userDto.email,
                        telefono = userDto.phone,
                        token = loginResponse.token,
                        role = userDto.roles
                    )

                    userPreferences.saveUser(user)
                    Log.d(
                        "AuthRepo",
                        "Usuario guardado con perfil: email=${user.email}, role='${user.role}', token=${user.token}"
                    )
                    Result.success(user)
                } catch (e: Exception) {
                    // Si falla obtener el perfil, guardar con datos mínimos
                    val user = User(
                        id = "",
                        nombre = "",
                        apellido = "",
                        email = email,
                        telefono = "",
                        role = "",
                        token = loginResponse.token
                    )
                    userPreferences.saveUser(user)
                    Log.d(
                        "AuthRepo",
                        "Usuario guardado con datos mínimos: token=${user.token}, email=${user.email}"
                    )
                    Result.success(user)

                }
            } else {
                Result.failure(Exception("Token vacío en la respuesta"))
            }
        } catch (e: HttpException) {
            val errorMessage = when (e.code()) {
                400 -> "Datos inválidos"
                401 -> "Email o contraseña incorrectos"
                404 -> "Usuario no encontrado"
                422 -> "Datos de entrada no válidos"
                500 -> "Error interno del servidor"
                502, 503 -> "Servicio no disponible temporalmente"
                else -> "Error de conexión (${e.code()})"
            }
            Result.failure(Exception(errorMessage))
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun register(
        name: String,
        lastname: String,
        email: String,
        phone: String,
        password: String,
        passwordRepeat: String
    ): Result<User> {
        return try {
            // Obteniene el token del usuario actual (debe ser ADMIN)
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))


            // Crear el request DTO
            val request = RegisterRequestDto(
                email = email.trim(),
                name = name.trim(),
                lastname = lastname.trim(),
                phone = phone.trim(),
                password = password,
                passwordRepeat = passwordRepeat,
                rol = "USER"
            )

            // Llamar al API CON TOKEN
            val registerResponse = apiService.register(
                token = "Bearer $currentToken",  // PRIMER parámetro: token
                request = request                 // SEGUNDO parámetro: request
            )

            // 4. Crear objeto User temporal (sin token, sin guardar sesión)
            val user = User(
                id = "",
                nombre = registerResponse.name,
                apellido = "",
                email = registerResponse.email,
                telefono = "",
                token = "",
                role = registerResponse.rol
            )

            Result.success(user)

        } catch (e: HttpException) {
            // PARSEAR EL ERROR BODY PARA DETECTAR CONFLICTOS ESPECÍFICOS
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (_: Exception) {
                null
            }

            Log.e("AuthRepo", "Error ${e.code()}: $errorBody");
            val errorMessage = when (e.code()) {
                400 -> "Datos inválidos"
                401 -> "No tienes permisos para registrar usuarios (debes ser ADMIN)"
                403 -> "Acceso denegado"
                // detectar si el error es por email o teléfono ya registrado -> teniamos que parsear el body pq habia un conflicto con el 409
                409 -> {
                    val message = errorBody ?: ""
                    when {
                        message.contains("Teléfono", ignoreCase = true) ->
                            "El teléfono ya está registrado"

                        message.contains("Usuario", ignoreCase = true) &&
                                message.contains("@", ignoreCase = false) ->
                            "El email ya está registrado"

                        else ->
                            "Ya existe un usuario con estos datos"
                    }
                }

                422 -> "Las contraseñas no coinciden o no cumplen los requisitos"
                500 -> "Error interno del servidor"
                502, 503 -> "Servicio no disponible temporalmente"
                else -> "Error de conexión (${e.code()})"
            }
            Result.failure(Exception(errorMessage))
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet. Verifica tu conexión."))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            val currentUser = getCurrentUser()
            if (currentUser != null) {
                try {
                    apiService.logout("Bearer ${currentUser.token}")
                } catch (_: Exception) {
                    // Ignorar errores de logout
                }
            }
            userPreferences.clearUser() // Limpiar sesión local
            Result.success(Unit)
        } catch (_: Exception) {
            // Ignoramos errores de logout, incluso si falla la llamada, devolver OK!
            Result.success(Unit)
        }
    }


    override suspend fun getProfile(): Result<User> {
        return try {
            // Obtener token guardado localmente
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            // Hacer request al backend
            val response = apiService.getProfile("Bearer $currentToken")

            // Convertir a domain pasando el token que ya tenemos
            val user = response.toDomain(currentToken)

            // Actualizar datos locales con los datos frescos
            saveUserSession(user)

            Result.success(user)

        } catch (e: HttpException) {
            val errorMessage = when (e.code()) {
                401 -> "Sesión expirada. Por favor, inicia sesión nuevamente"
                404 -> "Usuario no encontrado"
                500 -> "Error interno del servidor"
                else -> "Error al obtener perfil"
            }
            Result.failure(Exception(errorMessage))
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet"))
        } catch (e: Exception) {
            Result.failure(Exception("Error: ${e.message}"))
        }
    }

    override suspend fun updateProfile(
        nombre: String,
        apellido: String,
        email: String,
        telefono: String
    ): Result<User> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val request = UpdateProfileRequestDto(
                name = nombre,
                lastname = apellido,
                email = email,
                phone = telefono
            )

            val response = apiService.updateProfile("Bearer $currentToken", request)

            // Si la respuesta trae datos, usarlos
            val user = if (response.data != null) {
                response.data.toDomain(currentToken)
            } else {
                // Si no trae datos, volver a obtener el perfil actualizado
                val profileResponse = apiService.getProfile("Bearer $currentToken")
                profileResponse.toDomain(currentToken)
            }

            saveUserSession(user)

            Result.success(user)

        } catch (e: HttpException) {
            val errorMessage = when (e.code()) {
                400 -> "Datos inválidos"
                401 -> "Sesión expirada"
                422 -> "El email ya está en uso"
                else -> "Error al actualizar perfil"
            }
            Result.failure(Exception(errorMessage))
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet"))
        } catch (e: Exception) {
            Result.failure(Exception("Error: ${e.message}"))
        }
    }

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String
    ): Result<Unit> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val request = ChangePasswordRequestDto(
                password = newPassword,
                passwordRepeat = newPassword
            )

            apiService.changePassword("Bearer $currentToken", request)

            Result.success(Unit)

        } catch (e: HttpException) {
            val errorMessage = when (e.code()) {
                400 -> "Las contraseñas no coinciden"
                401 -> "Sesión expirada"
                422 -> "La contraseña no cumple los requisitos"
                else -> "Error al cambiar contraseña"
            }
            Result.failure(Exception(errorMessage))
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet"))
        } catch (e: Exception) {
            Result.failure(Exception("Error: ${e.message}"))
        }
    }


    override suspend fun deleteAccountByEmail(email: String): Result<String> {
        return try {
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            // Llamar API para eliminar cuenta por email (admin)
            val response = apiService.deleteAccount(
                email = email,
                token = "Bearer $currentToken"
            )

            Result.success(response.message)

        } catch (e: HttpException) {
            val errorMessage = when (e.code()) {
                404 -> "Usuario no encontrado"
                401 -> "Sesión expirada"
                403 -> "No tienes permisos para eliminar cuentas"
                500 -> "Error interno del servidor"
                else -> "Error al eliminar cuenta (${e.code()})"
            }
            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet"))
        } catch (e: Exception) {
            Result.failure(Exception("Error: ${e.message}"))
        }
    }

    override suspend fun getDirectory(forceRefresh: Boolean): Result<List<DirectoryUserDto>> {
        return try {
            // Si NO se fuerza refresh Y el cache es válido, usar cache
            if (!forceRefresh && directoryPreferences.isCacheValid()) {
                val cached = directoryPreferences.getUsers()
                if (cached != null) {
                    return Result.success(cached)
                }
            }

            // Obtener de API
            val currentToken = userPreferences.getUserToken()
                ?: return Result.failure(Exception("No hay sesión activa"))

            val response = apiService.getDirectory("Bearer $currentToken")

            // Guardar en cache para próximas veces
            directoryPreferences.saveUsers(response)

            Result.success(response)

        } catch (e: HttpException) {
            // Si falla la API, intentar usar cache aunque esté expirado, entonces devolverlo
            val cached = directoryPreferences.getUsers()
            if (cached != null) {
                // Devolver cache aunque esté desactualizado
                return Result.success(cached)
            }

            val errorMessage = when (e.code()) {
                401 -> "Sesión expirada. Por favor, inicia sesión nuevamente"
                403 -> "No tienes permisos para ver el directorio"
                404 -> "Directorio no encontrado"
                500 -> "Error interno del servidor"
                502, 503 -> "Servicio no disponible temporalmente"
                else -> "Error al obtener directorio (${e.code()})"
            }
            Result.failure(Exception(errorMessage))

        } catch (e: IOException) {
            // Sin internet: usar cache si existe
            val cached = directoryPreferences.getUsers()
            if (cached != null) {
                return Result.success(cached)
            }
            Result.failure(Exception("Sin conexión a internet y sin datos locales"))

        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    override suspend fun getCurrentUser(): User? {
        return userPreferences.getUser()
    }

    override suspend fun saveUserSession(user: User) {
        userPreferences.saveUser(user)
    }

    override suspend fun clearUserSession() {
        userPreferences.clearUser()
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return userPreferences.isLoggedIn()
    }
}
