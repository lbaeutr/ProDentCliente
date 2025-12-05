package dev.luisbaena.prodentclient.domain.usecase.work

import dev.luisbaena.prodentclient.data.remote.dto.work.WorkListDto
import dev.luisbaena.prodentclient.domain.repository.WorkRepository
import javax.inject.Inject

    /**
     * UseCase para búsqueda global de trabajos
     * Busca en: número de trabajo, paciente, clínica, dentista, tipo de trabajo, material
     */
class SearchWorkUseCase @Inject constructor(
    private val workRepository: WorkRepository
) {
    suspend operator fun invoke(query: String): Result<List<WorkListDto>> {
        // Validar que la búsqueda no esté vacía
        if (query.isBlank()) {
            return Result.failure(Exception("La búsqueda no puede estar vacía"))
        }

        return workRepository.searchGlobal(query)
    }
}
