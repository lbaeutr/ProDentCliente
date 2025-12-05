package dev.luisbaena.prodentclient.domain.model

    /**
     * Modelo de dominio para clínica
     */
data class Clinic(
    val id: String = "",
    val nombre: String,
    val direccion: String = "",
    val telefono: String = "",
    val email: String = "",
    val horarios: Horarios? = null,
    val activa: Boolean = true,
    val fechaRegistro: String? = null,
    val observaciones: String? = null
) {
    fun getDisplayName(): String = nombre

    fun getContact(): String = telefono.ifEmpty { email }

    fun getFullAddress(): String = direccion

    fun isActive(): Boolean = activa
}
