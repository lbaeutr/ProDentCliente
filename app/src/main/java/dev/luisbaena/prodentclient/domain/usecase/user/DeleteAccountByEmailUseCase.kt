package dev.luisbaena.prodentclient.domain.usecase.user

import dev.luisbaena.prodentclient.domain.repository.AuthRepository
import javax.inject.Inject

    /**
     * UseCase para eliminar una cuenta de usuario por correo electrónico
     */
class DeleteAccountByEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<String> {
        return authRepository.deleteAccountByEmail(email)
    }
}

