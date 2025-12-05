package dev.luisbaena.prodentclient.data.remote.dto.user

import kotlinx.serialization.Serializable

    /**
     * DTO para la respuesta de eliminación de cuenta de usuario
     */
@Serializable
data class DeleteAccountResponseDto(
    val message: String,
    val status: Int? = null,
    val error: String? = null,
    val timestamp: String? = null,
    val path: String? = null
)
