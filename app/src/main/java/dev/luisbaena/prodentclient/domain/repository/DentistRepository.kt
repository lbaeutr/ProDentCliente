package dev.luisbaena.prodentclient.domain.repository

import dev.luisbaena.prodentclient.data.remote.dto.dentist.DentistListItemDTO
import dev.luisbaena.prodentclient.data.remote.dto.dentist.DentistCreateRequestDTO
import dev.luisbaena.prodentclient.data.remote.dto.dentist.DentistCreateResponseDTO
import dev.luisbaena.prodentclient.data.remote.dto.dentist.DentistDto

    /**
     * Repository para gestión de dentistas
     */
interface DentistRepository {
    suspend fun getDentists(forceRefresh: Boolean = false): Result<List<DentistListItemDTO>>
    suspend fun getDentistById(id: String): Result<DentistDto>
    suspend fun createDentist(dentist: DentistCreateRequestDTO): Result<DentistCreateResponseDTO>
    suspend fun updateDentist(id: String, dentist: DentistDto): Result<DentistCreateResponseDTO>
    suspend fun deleteDentist(id: String): Result<Unit>
}