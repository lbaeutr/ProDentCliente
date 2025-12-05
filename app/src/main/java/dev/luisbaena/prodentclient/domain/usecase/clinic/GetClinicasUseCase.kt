package dev.luisbaena.prodentclient.domain.usecase.clinic

import dev.luisbaena.prodentclient.domain.model.Clinic
import dev.luisbaena.prodentclient.domain.repository.ClinicRepository
import javax.inject.Inject

    /**
     * UseCase para obtener la lista de clínicas
     */
class GetClinicasUseCase @Inject constructor(
    private val clinicRepository: ClinicRepository
) {
    suspend operator fun invoke(forceRefresh: Boolean = false): Result<List<Clinic>> {
        return clinicRepository.getClinicas(forceRefresh)
    }
}