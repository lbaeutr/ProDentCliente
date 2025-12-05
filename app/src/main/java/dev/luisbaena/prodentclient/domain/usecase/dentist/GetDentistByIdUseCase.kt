package dev.luisbaena.prodentclient.domain.usecase.dentist

import dev.luisbaena.prodentclient.data.remote.dto.dentist.DentistDto
import dev.luisbaena.prodentclient.domain.repository.DentistRepository
import javax.inject.Inject

    /**
     * UseCase para obtener un dentista por ID
     */
class GetDentistByIdUseCase @Inject constructor(
    private val dentistRepository: DentistRepository
) {
    suspend operator fun invoke(id: String): Result<DentistDto> {
        return dentistRepository.getDentistById(id)
    }
}