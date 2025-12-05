package dev.luisbaena.prodentclient.data.remote.dto.workType

import kotlinx.serialization.Serializable

    /**
     * DTO para registro/creación de tipo de trabajo
     */
@Serializable
data class WorkTypeRequestDTO(
    val categoria: String,
    val nombre: String,
    val descripcion: String? = null,
    val precioBase: Double? = null
)