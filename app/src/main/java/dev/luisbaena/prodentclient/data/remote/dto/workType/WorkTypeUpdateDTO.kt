package dev.luisbaena.prodentclient.data.remote.dto.workType

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

    /**
     * DTO para actualización de tipo de trabajo
     * Todos los campos son opcionales (PATCH)
     */
@Serializable
data class WorkTypeUpdateDTO(
    val categoria: String? = null,
    val nombre: String? = null,
    val descripcion: String? = null,
    val precioBase: Double? = null,
    val activo: Boolean? = null
)