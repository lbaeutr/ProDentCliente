package dev.luisbaena.prodentclient.data.remote.dto.work

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

    /**
     * DTO para actualización de trabajo
     * Todos los campos son opcionales (PATCH)
     */
@Serializable
data class WorkUpdateDTO(
    @SerialName("numeroTrabajo")
    val numeroTrabajo: String? = null,
    @SerialName("clinicaId")
    val clinicaId: String? = null,
    @SerialName("dentistaId")
    val dentistaId: String? = null,
    @SerialName("tipoTrabajoId")
    val tipoTrabajoId: String? = null,
    @SerialName("materialId")
    val materialId: String? = null,
    @SerialName("pacienteNombre")
    val pacienteNombre: String? = null,
    @SerialName("descripcionTrabajo")
    val descripcionTrabajo: String? = null,
    @SerialName("piezasDentales")
    val piezasDentales: String? = null,
    @SerialName("color")
    val color: String? = null,
    @SerialName("guiaColor")
    val guiaColor: String? = null,
    @SerialName("observaciones")
    val observaciones: String? = null,
    @SerialName("precio")
    val precio: Double? = null,
    @SerialName("ajustePrecio")
    val ajustePrecio: String? = null,
    @SerialName("urgente")
    val urgente: Boolean? = null,
    @SerialName("fechaEntregaEstimada")
    val fechaEntregaEstimada: String? = null // LocalDate COMO String
)