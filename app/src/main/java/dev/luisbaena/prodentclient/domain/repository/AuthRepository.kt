package dev.luisbaena.prodentclient.domain.repository

import dev.luisbaena.prodentclient.data.remote.dto.user.DirectoryUserDto
import dev.luisbaena.prodentclient.domain.model.User

    /**
     * Define operaciones de autenticacion del usuario.
     */
interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(
        name: String,
        lastname: String,
        email: String,
        phone: String,
        password: String,
        passwordRepeat: String
    ): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): User?
    suspend fun saveUserSession(user: User)
    suspend fun clearUserSession()
    suspend fun isUserLoggedIn(): Boolean
    suspend fun getProfile(): Result<User>
    suspend fun updateProfile(
        nombre: String,
        apellido: String,
        email: String,
        telefono: String,
    ): Result<User>

    suspend fun changePassword(
        oldPassword: String,
        newPassword: String
    ): Result<Unit>
    suspend fun deleteAccountByEmail(email: String): Result<String>
    suspend fun getDirectory(forceRefresh: Boolean = false): Result<List<DirectoryUserDto>>

}