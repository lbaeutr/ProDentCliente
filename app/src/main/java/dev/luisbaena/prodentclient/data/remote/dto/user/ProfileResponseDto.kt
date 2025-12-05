package dev.luisbaena.prodentclient.data.remote.dto.user

import dev.luisbaena.prodentclient.domain.model.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponseDto(
    @SerialName("success")
    val success: Boolean? = true,
    @SerialName("message")
    val message: String? = null,
    @SerialName("data")
    val data: ProfileUserDto? = null
)

    /**
     * DTO específico para GET /me (obtener perfil)
     * Contiene ID pero NO TOKEN (ya lo tenemos guardado)
     */
@Serializable
data class ProfileUserDto(
    val name: String,
    val lastname: String,
    val email: String,
    val phone: String,
    val roles: String
) {
    fun toDomain(token: String): User {
        return User(
            id = "",
            nombre = name,
            apellido = lastname,
            email = email,
            telefono = phone,
            token = token,  // Pasamos el token que ya tenemos guardado
            role = roles
        )
    }
}