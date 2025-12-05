package dev.luisbaena.prodentclient.data.remote.dto.dentist

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

    /**
     * DTO para listar dentistas (GET /dentistas/list)
     * Respuesta simplificada del backend con solo información básica
     */
@Serializable
data class DentistListItemDTO(
    @SerialName("id")
    val id: String,
    val nombre: String,
    val apellidos: String,
    val clinicaNombre: String,
    val activo: Boolean
)

