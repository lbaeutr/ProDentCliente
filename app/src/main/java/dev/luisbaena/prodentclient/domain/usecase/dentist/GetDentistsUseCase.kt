package dev.luisbaena.prodentclient.domain.usecase.dentist

import dev.luisbaena.prodentclient.data.remote.dto.dentist.DentistListItemDTO
import dev.luisbaena.prodentclient.domain.repository.DentistRepository
import javax.inject.Inject

    /**
     * UseCase para obtener lista de dentistas
     */
class GetDentistsUseCase @Inject constructor(
    private val dentistRepository: DentistRepository
) {
    suspend operator fun invoke(forceRefresh: Boolean = false): Result<List<DentistListItemDTO>> {
        return dentistRepository.getDentists(forceRefresh)
    }
}