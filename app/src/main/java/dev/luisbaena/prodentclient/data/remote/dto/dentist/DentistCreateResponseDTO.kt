package dev.luisbaena.prodentclient.data.remote.dto.dentist

import kotlinx.serialization.Serializable

    /**
     * DTO unificado para respuestas del backend sobre dentistas
     * NOTA: El backend usa "id" (no "_id") en las respuestas actuales
     */
@Serializable
data class DentistCreateResponseDTO(
    val id: String,
    val nombre: String,
    val apellidos: String,
    val email: String? = null,
    val telefono: String? = null,
    val numeroColegial: String? = null,
    val clinicaId: String? = null,
    val clinicaNombre: String? = null,
    val clinicaTelefono: String? = null,
    val activo: Boolean = true,
    val fechaRegistro: String? = null
)

