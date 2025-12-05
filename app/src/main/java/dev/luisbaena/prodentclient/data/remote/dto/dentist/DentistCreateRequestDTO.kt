package dev.luisbaena.prodentclient.data.remote.dto.dentist

import kotlinx.serialization.Serializable

    /**
     * DTO para crear o actualizar un dentista
     * Todos los campos son obligatorios (sin valores por defecto)
     * para garantizar que se serialicen en el JSON
     */
@Serializable
data class DentistCreateRequestDTO(
    val clinicaId: String,
    val nombre: String,
    val apellidos: String,
    val numeroColegial: String,
    val telefono: String,
    val email: String
)