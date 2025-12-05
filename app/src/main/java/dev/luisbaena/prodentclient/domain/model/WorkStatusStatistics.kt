package dev.luisbaena.prodentclient.domain.model

    /**
     * Modelo de dominio para estadísticas de trabajos por estado
     */
data class WorkStatusStatistics(
    val status: WorkStatus,
    val count: Int
)

