package dev.luisbaena.prodentclient.domain.usecase.clinic

import dev.luisbaena.prodentclient.domain.model.Clinic
import dev.luisbaena.prodentclient.domain.repository.ClinicRepository
import javax.inject.Inject

    /**
     * UseCase para crear una nueva clínica
     */

class CreateClinicaUseCase @Inject constructor(
    private val clinicRepository: ClinicRepository
) {
    suspend operator fun invoke(clinica: Clinic): Result<Clinic> {
        return clinicRepository.createClinica(clinica)
    }
}
