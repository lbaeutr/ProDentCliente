package dev.luisbaena.prodentclient.data.remote.dto.work

import kotlinx.serialization.Serializable

    /**
     * DTO PARA CAMBIO DE ESTADO DE TRABAJO
     * Contiene el nuevo estado y observaciones opcionales
     */
@Serializable
data class WorkChangeStatusDTO(
    val nuevoEstado: String, // ESCAYOLA, METAL_LAB, METAL_CAD, RESINA_LAB, CLINICA_CLIENTE, CANCELADO, TERMINADO, PARADO, etc.
    val observaciones: String? = null
)