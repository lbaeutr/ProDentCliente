package dev.luisbaena.prodentclient.domain.usecase.user

import android.util.Patterns
import dev.luisbaena.prodentclient.domain.model.User
import dev.luisbaena.prodentclient.domain.repository.AuthRepository
import javax.inject.Inject

    /**
     * UseCase para actualizar el perfil del usuario
     */
class UpdateProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        nombre: String,
        apellido: String,
        email: String,
        telefono: String
    ): Result<User> {
        // Validaciones
        if (nombre.isBlank()) {
            return Result.failure(Exception("El nombre no puede estar vacío"))
        }

        if (apellido.isBlank()) {
            return Result.failure(Exception("El apellido no puede estar vacío"))
        }

        if (email.isBlank()) {
            return Result.failure(Exception("El email no puede estar vacío"))
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("El formato del email no es válido"))
        }

        if (telefono.isBlank()) {
            return Result.failure(Exception("El teléfono no puede estar vacío"))
        }

        return authRepository.updateProfile(nombre, apellido, email, telefono)
    }
}