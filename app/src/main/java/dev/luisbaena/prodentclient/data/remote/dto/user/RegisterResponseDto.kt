package dev.luisbaena.prodentclient.data.remote.dto.user

import kotlinx.serialization.Serializable

    /**
     * DTO para el response de registro
     */
@Serializable
data class RegisterResponseDto(
    val name: String,
    val email: String,
    val rol: String
)
































