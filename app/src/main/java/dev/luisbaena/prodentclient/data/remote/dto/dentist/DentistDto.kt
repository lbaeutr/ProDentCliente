package dev.luisbaena.prodentclient.data.remote.dto.dentist

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

    /**
     * DTO para la respuesta del backend al obtener un dentista
     */
@Serializable
data class DentistDto(
    @SerialName("id")
    val id: String,

    // Referencia a clínica
    @SerialName("clinicaId")
    val clinicaId: String,

    // Datos desnormalizados de la clínica
    @SerialName("clinicaNombre")
    val clinicaNombre: String,

    @SerialName("clinicaTelefono")
    val clinicaTelefono: String,

    // Datos del dentista
    @SerialName("nombre")
    val nombre: String,

    @SerialName("apellidos")
    val apellidos: String,

    @SerialName("numeroColegial")
    val numeroColegial: String? = null,

    @SerialName("telefono")
    val telefono: String? = null,

    @SerialName("email")
    val email: String? = null,

    @SerialName("activo")
    val activo: Boolean = true,

    @SerialName("fechaRegistro")
    val fechaRegistro: String
)