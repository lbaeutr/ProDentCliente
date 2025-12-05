package dev.luisbaena.prodentclient.data.remote.dto.clinic

import dev.luisbaena.prodentclient.domain.model.Horarios
import kotlinx.serialization.Serializable

    /**
     * DTO para los horarios de la clínica
     */
@Serializable
data class HorariosDto(
    val lunes: String? = null,
    val martes: String? = null,
    val miercoles: String? = null,
    val jueves: String? = null,
    val viernes: String? = null
) {
    /**
     * Mapea el DTO a modelo de dominio
     */
    fun toDomain(): Horarios {
        return Horarios(
            lunes = lunes,
            martes = martes,
            miercoles = miercoles,
            jueves = jueves,
            viernes = viernes
        )
    }
}
