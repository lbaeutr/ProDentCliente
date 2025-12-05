package dev.luisbaena.prodentclient.domain.usecase.clinic


import dev.luisbaena.prodentclient.domain.model.Clinic
import dev.luisbaena.prodentclient.domain.repository.ClinicRepository
import javax.inject.Inject

    /**
     * UseCase para obtener una clínica específica por ID
     */
class GetClinicaByIdUseCase @Inject constructor(
    private val clinicRepository: ClinicRepository
) {
    suspend operator fun invoke(id: String): Result<Clinic> {
        return clinicRepository.getClinicaById(id)
    }
}
