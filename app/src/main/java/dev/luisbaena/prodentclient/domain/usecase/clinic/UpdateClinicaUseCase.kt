package dev.luisbaena.prodentclient.domain.usecase.clinic

import dev.luisbaena.prodentclient.domain.model.Clinic
import dev.luisbaena.prodentclient.domain.repository.ClinicRepository
import javax.inject.Inject

    /**
     * UseCase para actualizar una clínica existente
     */
class UpdateClinicaUseCase @Inject constructor(
    private val clinicRepository: ClinicRepository
) {
    suspend operator fun invoke(id: String, clinica: Clinic): Result<Clinic> {
        return clinicRepository.updateClinica(id, clinica)
    }
}