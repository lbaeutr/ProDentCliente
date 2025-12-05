package dev.luisbaena.prodentclient.domain.model

    /**
     * Modelo de dominio para usuario
     */
data class User(
    val id: String,
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String,
    val token: String,
    val role: String,
){
    // Obtiene el nombre completo del usuario
    fun getFullName(): String {
        return "$nombre $apellido"
    }
}
