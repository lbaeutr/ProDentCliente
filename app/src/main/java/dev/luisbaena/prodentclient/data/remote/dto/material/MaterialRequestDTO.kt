package dev.luisbaena.prodentclient.data.remote.dto.material

import kotlinx.serialization.Serializable

    /**
     * DTO para registro/creación de material
     */
@Serializable
data class MaterialRequestDTO(
    val nombre: String,
    val descripcion: String? = null
)