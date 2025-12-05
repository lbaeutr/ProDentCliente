package dev.luisbaena.prodentclient.domain.usecase.user

import dev.luisbaena.prodentclient.data.remote.dto.user.DirectoryUserDto
import dev.luisbaena.prodentclient.domain.repository.AuthRepository
import javax.inject.Inject

    /**
     * UseCase para obtener el directorio de usuarios
     */
class GetDirectoryUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
     suspend operator fun invoke(forceRefresh: Boolean = false): Result<List<DirectoryUserDto>> {
        return authRepository.getDirectory(forceRefresh)
    }
}














































