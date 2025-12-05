package dev.luisbaena.prodentclient.domain.model

    /**
     * Image type enum
     * Tipos de imágenes que se pueden asociar a un trabajo
     */
enum class ImageType(val value: String, val displayName: String) {
    GENERAL("GENERAL", "General"),
    BEFORE("ANTES", "Antes"),
    DURING("DURANTE", "Durante"),
    AFTER("DESPUES", "Después"),
    PRESCRIPTION("PRESCRIPCION", "Prescripción"),
    XRAY("RADIOGRAFIA", "Radiografía"),
    SCAN_3D("ESCANEO_3D", "Escaneo 3D");

        // Función para obtener un ImageType a partir de su valor en String
    companion object {
        fun fromValue(value: String): ImageType? {
            return entries.find { it.value == value }
        }
    }
}