package dev.luisbaena.prodentclient.data.remote.dto.clinic

import kotlinx.serialization.Serializable
    /**
     * DTO para actualizar una clínica
     */
@Serializable
data class ClinicUpdateDTO(
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val email: String,
    val horarios: HorariosDto,
    val activa: Boolean,
    val observaciones: String? = null
)
