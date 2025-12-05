package dev.luisbaena.prodentclient.domain.usecase.dentist

import dev.luisbaena.prodentclient.data.remote.dto.dentist.DentistCreateResponseDTO
import dev.luisbaena.prodentclient.data.remote.dto.dentist.DentistCreateRequestDTO
import dev.luisbaena.prodentclient.domain.repository.DentistRepository
import javax.inject.Inject

    /**
     * UseCase para crear un nuevo dentista
     */
class CreateDentistUseCase @Inject constructor(
    private val dentistRepository: DentistRepository
) {
    suspend operator fun invoke(dentist: DentistCreateRequestDTO): Result<DentistCreateResponseDTO> {
        return dentistRepository.createDentist(dentist)
    }
}