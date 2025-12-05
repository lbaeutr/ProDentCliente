package dev.luisbaena.prodentclient.domain.usecase.user

import dev.luisbaena.prodentclient.domain.repository.AuthRepository
import javax.inject.Inject

    /**
     * UseCase para cambiar la contraseña del usuario
     */
class ChangePasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        newPassword: String,
        confirmPassword: String
    ): Result<Unit> {
        // Validaciones
        if (newPassword.isBlank()) {
            return Result.failure(Exception("La contraseña no puede estar vacía"))
        }

        if (newPassword.length < 6) {
            return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
        }

        if (newPassword != confirmPassword) {
            return Result.failure(Exception("Las contraseñas no coinciden"))
        }

        return authRepository.changePassword(newPassword, confirmPassword)
    }
}