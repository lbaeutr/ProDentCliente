package dev.luisbaena.prodentclient.domain.model

    /**
     * Payment method enum
     * Métodos de pago directo (sin factura)
     */
enum class PaymentMethod(val value: String, val displayName: String) {
    CASH("EFECTIVO", "Efectivo"),
    CARD("TARJETA", "Tarjeta"),
    TRANSFER("TRANSFERENCIA", "Transferencia"),
    BIZUM("BIZUM", "Bizum");

        // Función para obtener un PaymentMethod a partir de su valor en String
    companion object {
        fun fromValue(value: String): PaymentMethod? {
            return PaymentMethod.entries.find { it.value == value }
        }
    }
}