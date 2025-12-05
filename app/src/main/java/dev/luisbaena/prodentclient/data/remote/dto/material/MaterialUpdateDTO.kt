package dev.luisbaena.prodentclient.data.remote.dto.material

import kotlinx.serialization.Serializable

    /**
     * DTO para actualización de material
     * Todos los campos son opcionales (PATCH)
     */
@Serializable
data class MaterialUpdateDTO(
    val nombre: String? = null,
    val descripcion: String? = null,
    val activo: Boolean? = null
)