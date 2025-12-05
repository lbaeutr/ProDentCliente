package dev.luisbaena.prodentclient.data.remote.dto.user


import kotlinx.serialization.Serializable

    /**
     * DTO para la respuesta del login
     */
@Serializable
data class LoginResponseDto(
    val token: String,
)
