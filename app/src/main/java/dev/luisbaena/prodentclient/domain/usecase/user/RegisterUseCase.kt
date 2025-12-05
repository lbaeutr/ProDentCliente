package dev.luisbaena.prodentclient.domain.usecase.user

import dev.luisbaena.prodentclient.domain.model.User
import dev.luisbaena.prodentclient.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * UseCase para el registro de usuarios
 */
class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        name: String,
        lastname: String,
        email: String,
        phone: String,
        password: String,
        passwordRepeat: String
    ): Result<User> {
        // Validaciones básicas antes de llamar al repository
        if (name.isBlank()) {
            return Result.failure(Exception("El nombre es obligatorio"))
        }

        if (lastname.isBlank()) {
            return Result.failure(Exception("El apellido es obligatorio"))
        }

        if (email.isBlank()) {
            return Result.failure(Exception("El email es obligatorio"))
        }

        if (phone.isBlank()) {
            return Result.failure(Exception("El teléfono es obligatorio"))
        }

        if (password.isBlank()) {
            return Result.failure(Exception("La contraseña es obligatoria"))
        }

        if (password.length < 6) {
            return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
        }

        if (password != passwordRepeat) {
            return Result.failure(Exception("Las contraseñas no coinciden"))
        }

        // Llamar al repository
        return authRepository.register(
            name = name.trim(),
            lastname = lastname.trim(),
            email = email.trim(),
            phone = phone.trim(),
            password = password,
            passwordRepeat = passwordRepeat
        )
    }
}