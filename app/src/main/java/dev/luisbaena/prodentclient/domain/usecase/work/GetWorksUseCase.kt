package dev.luisbaena.prodentclient.domain.usecase.work

import dev.luisbaena.prodentclient.data.remote.dto.work.WorkListDto
import dev.luisbaena.prodentclient.domain.repository.WorkRepository
import javax.inject.Inject

    /**
     * UseCase para obtener la lista de trabajos con filtros opcionales
     */
class GetWorksUseCase @Inject constructor(
    private val workRepository: WorkRepository
) {
    suspend operator fun invoke(
        clinicaId: String? = null,
        dentistaId: String? = null,
        protesicoId: String? = null,
        estado: String? = null,
        urgente: Boolean? = null,
        forceRefresh: Boolean = false
    ): Result<List<WorkListDto>> {
        return workRepository.getWorks(
            clinicaId = clinicaId,
            dentistaId = dentistaId,
            protesicoId = protesicoId,
            estado = estado,
            urgente = urgente,
            forceRefresh = forceRefresh
        )
    }
}