package dev.luisbaena.prodentclient.data.remote.dto.user

import kotlinx.serialization.Serializable

    /**
     * DTO para el cambio de contraseña de usuario
     */
@Serializable
data class ChangePasswordRequestDto(
    val password: String,
    val passwordRepeat: String
)