package dev.luisbaena.prodentclient.domain.usecase.user

import dev.luisbaena.prodentclient.domain.model.User
import dev.luisbaena.prodentclient.domain.repository.AuthRepository
import javax.inject.Inject


    /**
     * UseCase para obtener el usuario actualmente autenticado
     */
class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): User? {
        return authRepository.getCurrentUser()
    }
}