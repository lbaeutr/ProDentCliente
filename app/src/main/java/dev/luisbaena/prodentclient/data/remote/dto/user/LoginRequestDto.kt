package dev.luisbaena.prodentclient.data.remote.dto.user

import kotlinx.serialization.Serializable

    /**
     * DTO para el request de login
     */
@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String
)
