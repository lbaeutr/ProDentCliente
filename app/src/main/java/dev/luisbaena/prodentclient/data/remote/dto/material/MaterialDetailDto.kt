package dev.luisbaena.prodentclient.data.remote.dto.material

import kotlinx.serialization.Serializable

    /**
     * DTO completo para detalles de material
     */
@Serializable
data class MaterialDetailDto(
    val id: String,
    val nombre: String,
    val descripcion: String? = null,
    val activo: Boolean = true,
    val fechaCreacion: String // LocalDateTime como String en ISO format
)