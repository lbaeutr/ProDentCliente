package dev.luisbaena.prodentclient.data.remote.dto.work


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

    /**
     * DTO PARA CREACIÓN O ACTUALIZACIÓN DE TRABAJO
     * NO TIENE EL CAMPO IMAGENES QUE SE GESTIONAN POR SEPARADO
     */
@Serializable
data class WorkRequestDTO(
    @SerialName("numeroTrabajo")
    val numeroTrabajo: String,


    // REFERENCIAS  REQUERIDAS
    @SerialName("clinicaId")
    val clinicaId: String,
    @SerialName("dentistaId")
    val dentistaId: String,
    @SerialName("tipoTrabajoId")
    val tipoTrabajoId: String,
    @SerialName("materialId")
    val materialId: String? = null, // Optional


    // INFORMACION DEL PACIENTE Y TRABAJO
    @SerialName("pacienteNombre")
    val pacienteNombre: String,
    @SerialName("piezasDentales")
    val piezasDentales: String, // Ex: "11, 12, 13"
    @SerialName("precio")
    val precio: Double,

    // INFORMACION ADICIONAL
    @SerialName("color")
    val color: String? = null,
    @SerialName("guiaColor")
    val guiaColor: String? = null,
    @SerialName("descripcionTrabajo")
    val descripcionTrabajo: String? = null,
    @SerialName("observaciones")
    val observaciones: String? = null,
    @SerialName("ajustePrecio")
    val ajustePrecio: String? = null,

    // FECHAS  Y ESTADO
    @SerialName("urgente")
    val urgente: Boolean = false,
    @SerialName("fechaEntregaEstimada")
    val fechaEntregaEstimada: String? = null, // LocalDate COMO String

    // TODO futura implementación de protesico
    @SerialName("protesicoId")
    val protesicoId: String? = null
)