package dev.luisbaena.prodentclient.data.remote.dto.material

import kotlinx.serialization.Serializable

    /**
     * DTO simple para listados de materiales
     */
@Serializable
data class MaterialListDto(
    val id: String,
    val nombre: String,
    val activo: Boolean = true
)