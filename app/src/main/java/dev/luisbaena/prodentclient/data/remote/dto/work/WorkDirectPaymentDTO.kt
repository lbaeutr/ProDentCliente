package dev.luisbaena.prodentclient.data.remote.dto.work

import kotlinx.serialization.Serializable

    /**
     * DTO PARA REGISTRAR PAGO DIRECTO DE TRABAJO   Y ACTUALIZAR ESTADO A PAGADO AUTOMATICAMENTE
     */
@Serializable
data class WorkDirectPaymentDTO(
    val fechaPago: String, // LocalDate COMO String (formato: "2025-11-12")
    val metodoPago: String // EFECTIVO, TARJETA, TRANSFERENCIA, BIZUM....
)