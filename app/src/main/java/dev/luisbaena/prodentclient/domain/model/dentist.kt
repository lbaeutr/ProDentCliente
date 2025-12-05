package dev.luisbaena.prodentclient.domain.model

    /**
     * Entidad Dentista
     */
data class Dentist(
    val id: String,

    // Referencia completa a clínica
    val clinicaId: String,

    // Datos desnormalizados para lectura rápida
    val clinicaNombre: String,
    val clinicaTelefono: String,

    // Datos del dentista
    val nombre: String,
    val apellidos: String,
    val numeroColegial: String?,
    val telefono: String?,
    val email: String?,

    val activo: Boolean,
    val fechaRegistro: String
)