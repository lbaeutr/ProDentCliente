package dev.luisbaena.prodentclient.data.remote.dto.clinic

import dev.luisbaena.prodentclient.domain.model.Clinic
import kotlinx.serialization.Serializable


    /**
     * DTO para una clínica dental
     */
@Serializable
data class ClinicRequestDTO(
    val id: String,
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val email: String,
    val horarios: HorariosDto,
    val activa: Boolean,
    val fechaRegistro: String,
    val observaciones: String? = null
) {
    /**
     * Mapea el DTO a modelo de dominio
     */
    fun toDomain(): Clinic {
        return Clinic(
            id = id,
            nombre = nombre,
            direccion = direccion,
            telefono = telefono,
            email = email,
            horarios = horarios.toDomain(),
            activa = activa,
            fechaRegistro = fechaRegistro,
            observaciones = observaciones
        )
    }
}
