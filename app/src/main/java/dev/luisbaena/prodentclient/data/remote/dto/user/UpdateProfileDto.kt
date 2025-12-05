package dev.luisbaena.prodentclient.data.remote.dto.user

import kotlinx.serialization.Serializable

    /**
     * DTO para actualizar el perfil de un usuario
     */
@Serializable
data class UpdateProfileRequestDto(
    val name: String,
    val lastname: String,
    val email: String,
    val phone: String
)

