package dev.luisbaena.prodentclient.data.remote.dto.user

import kotlinx.serialization.Serializable


    /**
     * DTO para usuario del directorio
     * Representa la respuesta del endpoint GET /usuarios/directorio
     */
@Serializable
data class DirectoryUserDto(
    val name: String,
    val lastname: String,
    val email: String,
    val phone: String
)

/**
 * función de extensión para obtener el nombre completo del usuario
 */
fun DirectoryUserDto.fullName(): String {
    return "$name $lastname"
}
