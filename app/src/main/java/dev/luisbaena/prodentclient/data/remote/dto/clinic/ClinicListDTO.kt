package dev.luisbaena.prodentclient.data.remote.dto.clinic

import dev.luisbaena.prodentclient.domain.model.Clinic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

    /**
     * DTO simplificado para mostrar lista de clínicas
     * Los campos del backend vienen como: id, nombre, telefono, activa
     * Los mapeamos a: id, name, phone, isActive
     */
@Serializable
data class ClinicListDTO(
    @SerialName("id")
    val id: String,

    @SerialName("nombre")
    val name: String,

    @SerialName("telefono")
    val phone: String,

    @SerialName("activa")
    val isActive: String
) {
    /**
     * Mapea el DTO a modelo de dominio
     */
    fun toDomain(): Clinic {
        return Clinic(
            id = id,
            nombre = name,
            telefono = phone,
            activa = isActive == "true" || isActive == "1",
            direccion = "",
            email = "",
            horarios = null,
            fechaRegistro = null,
            observaciones = null
        )
    }
}
