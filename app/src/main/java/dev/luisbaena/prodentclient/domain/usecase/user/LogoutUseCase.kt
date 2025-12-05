package dev.luisbaena.prodentclient.domain.usecase.user

import dev.luisbaena.prodentclient.domain.repository.AuthRepository
import javax.inject.Inject

    /**
     * UseCase para cerrar la sesión del usuario
     */

    // Este codigo cierra la sesión del usuario llamando al repositorio de autenticación.
class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit>{
        return try {
            authRepository.clearUserSession()
            authRepository.logout()

        } catch (e: Exception){

            authRepository.clearUserSession()
            Result.success(Unit)
        }
    }
}