package dev.luisbaena.prodentclient.data.remote.dto.work

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

    /**
     * DTO simplificado para estadísticas de trabajos por estado
     */
@Serializable
data class WorkStatusStatisticsDto(
    @SerialName("estado")
    val estado: String,
    @SerialName("cantidad")
    val cantidad: Int
)

