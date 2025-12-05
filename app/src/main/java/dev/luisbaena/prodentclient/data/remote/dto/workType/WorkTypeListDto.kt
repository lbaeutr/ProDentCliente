package dev.luisbaena.prodentclient.data.remote.dto.workType

import kotlinx.serialization.Serializable

    /**
     * DTO simple para listados de tipos de trabajo
     */
@Serializable
data class WorkTypeListDto(
    val id: String,
    val categoria: String,
    val nombre: String,
    val precioBase: Double? = null,
    val activo: Boolean = true
)