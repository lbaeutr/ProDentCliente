package dev.luisbaena.prodentclient.domain.repository

import dev.luisbaena.prodentclient.domain.model.Clinic

    /*
     * Repositorio para manejar las operaciones relacionadas con clínicas.
     */
interface ClinicRepository {
    suspend fun getClinicas(forceRefresh: Boolean = false): Result<List<Clinic>>
    suspend fun getClinicaById(id: String): Result<Clinic>

    suspend fun createClinica(clinica: Clinic): Result<Clinic>
    suspend fun updateClinica(id: String, clinica: Clinic): Result<Clinic>
    suspend fun deleteClinica(id: String): Result<Unit>
}