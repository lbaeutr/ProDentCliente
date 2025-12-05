package dev.luisbaena.prodentclient.data.remote.dto.user

import kotlinx.serialization.Serializable

    /**
     * DTO para el request de registro
     */
@Serializable
data class RegisterRequestDto(
    val email: String,
    val name: String,
    val lastname: String,
    val phone: String,
    val password: String,
    val passwordRepeat: String,
    val rol: String = "USER" // Valor por defecto para el rol
)