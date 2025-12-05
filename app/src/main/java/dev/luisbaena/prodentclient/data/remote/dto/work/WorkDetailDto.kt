package dev.luisbaena.prodentclient.data.remote.dto.work

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

    /**
     * DTO para detalle de trabajo
     *  contiene toda la información del trabajo
     */
@Serializable
data class WorkDetailDto(
    @SerialName("id")
    val id: String,
    @SerialName("numeroTrabajo")
    val numeroTrabajo: String,

    // CLINICA
    @SerialName("clinicaId")
    val clinicaId: String,
    @SerialName("clinicaNombre")
    val clinicaNombre: String,
    @SerialName("clinicaTelefono")
    val clinicaTelefono: String,

    // DENTISTA
    @SerialName("dentistaId")
    val dentistaId: String,
     @SerialName("dentistaNombre")
    val dentistaNombre: String,
    @SerialName("dentistaApellidos")
    val dentistaApellidos: String,

    // PACIENTE
    @SerialName("pacienteNombre")
    val pacienteNombre: String,


    // TIPO DE TRABAJO
    @SerialName("tipoTrabajoId")
    val tipoTrabajoId: String,
    @SerialName("tipoTrabajoNombre")
    val tipoTrabajoNombre: String,
    @SerialName("tipoTrabajoCategoria")
    val tipoTrabajoCategoria: String,
    @SerialName("descripcionTrabajo")
    val descripcionTrabajo: String? = null,


    // MATERIALES
    @SerialName("materialId")
    val materialId: String? = null,
    @SerialName("materialNombre")
    val materialNombre: String? = null,

    //  DETALLES TECNICOS  DEL TRABAJO
    @SerialName("piezasDentales")
    val piezasDentales: String, // EJ: "11, 12, 13"
    @SerialName("color")
    val color: String? = null,
    @SerialName("guiaColor")
    val guiaColor: String? = null,

    // ESTADO  DEL TRABAJO
    @SerialName("estado")
    val estado: String, // ESCAYOLA, METAL_LAB, METAL_CAD, RESINA_LAB, CLINICA_CLIENTE, CANCELADO, TERMINADO, PARADO, etc.
    @SerialName("urgente")
    val urgente: Boolean = false,

    // FECHAS
    @SerialName("fechaEntrada")
    val fechaEntrada: String, // LocalDateTime as String
    @SerialName("fechaEntregaEstimada")
    val fechaEntregaEstimada: String? = null, // LocalDate as String
    @SerialName("fechaEntregaReal")
    val fechaEntregaReal: String? = null, // LocalDate as String

    // PRÓTESICO ASIGNADO
    @SerialName("protesicoId")
    val protesicoId: String? = null,
    @SerialName("protesicoNombre")
    val protesicoNombre: String? = null,

    // OBSERVACIONES
    @SerialName("observaciones")
    val observaciones: String? = null,

    // PRECIO  Y AJUSTES
    @SerialName("precio")
    val precio: Double,
    @SerialName("ajustePrecio")
    val ajustePrecio: String? = null, // PRECIO, DESCUENTO, etc.

    // FACTURACIÓN  Y PAGO
    @SerialName("facturaId")
    val facturaId: String? = null,
    @SerialName("facturaNumero")
    val facturaNumero: String? = null,
    @SerialName("pagadoDirectamente")
    val pagadoDirectamente: Boolean = false,
    @SerialName("fechaPagoDirecto")
    val fechaPagoDirecto: String? = null, // LocalDate as String
    @SerialName("metodoPagoDirecto")
    val metodoPagoDirecto: String? = null, // EFECTIVO, TARJETA, etc.

    // IMAGENES ASOCIADAS  AL TRABAJO
    @SerialName("imagenes")
    val imagenes: List<WorkImageDto> = emptyList()
)