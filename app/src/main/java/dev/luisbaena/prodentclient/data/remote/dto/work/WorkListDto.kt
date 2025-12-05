package dev.luisbaena.prodentclient.data.remote.dto.work

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

    /**
     * DTO para lista de trabajos
     */
@Serializable
data class WorkListDto(
    @SerialName("id")
    val id: String,
    @SerialName("numeroTrabajo")
    val numeroTrabajo: String,
    @SerialName("clinicaNombre")
    val clinicaNombre: String,
    @SerialName("dentistaNombre")
    val dentistaNombre: String, // Full name (nombre + apellidos)
    @SerialName("pacienteNombre")
    val pacienteNombre: String,
    @SerialName("tipoTrabajoNombre")
    val tipoTrabajoNombre: String,
    @SerialName("estado")
    val estado: String, // ESCAYOLA, METAL_LAB, METAL_CAD, RESINA_LAB, CLINICA_CLIENTE, CANCELADO, TERMINADO, PARADO, etc.
    @SerialName("urgente")
    val urgente: Boolean = false,
    @SerialName("fechaEntrada")
    val fechaEntrada: String, // LocalDateTime as String
    @SerialName("fechaEntregaEstimada")
    val fechaEntregaEstimada: String? = null, // LocalDate as String
    @SerialName("precio")
    val precio: Double,
    @SerialName("cantidadImagenes")
    val cantidadImagenes: Int = 0
)