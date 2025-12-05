package dev.luisbaena.prodentclient.domain.usecase.dentist

import dev.luisbaena.prodentclient.data.remote.dto.dentist.DentistCreateResponseDTO
import dev.luisbaena.prodentclient.data.remote.dto.dentist.DentistDto
import dev.luisbaena.prodentclient.domain.repository.DentistRepository
import javax.inject.Inject

    /**
     * UseCase para actualizar un dentista existente
     */
class UpdateDentistUseCase @Inject constructor(
    private val dentistRepository: DentistRepository
) {
    suspend operator fun invoke(id: String, dentist: DentistDto): Result<DentistCreateResponseDTO> {
        return dentistRepository.updateDentist(id, dentist)
    }
}