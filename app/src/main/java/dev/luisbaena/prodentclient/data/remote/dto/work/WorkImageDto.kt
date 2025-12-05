package dev.luisbaena.prodentclient.data.remote.dto.work

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

    /**
     * DTO para imagen asociada a un trabajo
     */
@Serializable
data class WorkImageDto(
    @SerialName("id")
    val id: String,
    @SerialName("nombreArchivo")
    val nombreArchivo: String,
    @SerialName("contentType")
    val contentType: String,
    @SerialName("tamanio")
    val tamanio: Long,
    @SerialName("tipoImagen")
    val tipoImagen: String, // GENERAL, ANTES, DURANTE, DESPUES, etc.
    @SerialName("descripcion")
    val descripcion: String? = null,
    @SerialName("fechaSubida")
    val fechaSubida: String, // LocalDateTime as String
    @SerialName("urlDescarga")
    val urlDescarga: String // Download URL: /trabajos/{id}/imagenes/{imagenId}/download
)