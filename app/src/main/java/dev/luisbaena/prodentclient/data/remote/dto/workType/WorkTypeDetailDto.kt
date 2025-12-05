package dev.luisbaena.prodentclient.data.remote.dto.workType


import kotlinx.serialization.Serializable

    /**
     * DTO completo para detalles de tipo de trabajo
     */
@Serializable
data class WorkTypeDetailDto(
    val id: String,
    val categoria: String,
    val nombre: String,
    val descripcion: String? = null,
    val precioBase: Double? = null,
    val activo: Boolean = true,
    val fechaCreacion: String // LocalDateTime como String en ISO format
)