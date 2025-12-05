package dev.luisbaena.prodentclient.presentation.utils

    /**
     * Configuración centralizada de la URL base de la API.
     *
     * PROPÓSITO:
     * Convertir URLs relativas (que devuelve el backend) en URLs absolutas
     * para cargar imágenes con Coil/AsyncImage.
     *
     * PROBLEMA QUE RESUELVE:
     * El backend devuelve rutas relativas como: "/trabajos/123/imagenes/456/download"
     * Pero Coil necesita la URL completa: "https://prodent-api.onrender.com/trabajos/123/imagenes/456/download"
     *
     * IMPORTANTE:
     * - NO usar para llamadas REST (Retrofit ya tiene configurada la BASE_URL en DataModule.kt)
     * - Solo usar para cargar imágenes
     * - La URL NO debe terminar en "/" para evitar dobles barras
     *
     */
object ApiConfig {

    const val BASE_URL_NO_SLASH: String = "https://prodent-api.onrender.com"

    /**
     * Convierte una ruta relativa en URL absoluta.
     *
     * Ejemplos:
     * - "/trabajos/123/imagenes/456/download" → "https://prodent-api.onrender.com/trabajos/123/imagenes/456/download"
     * - "trabajos/123/imagenes/456/download"  → "https://prodent-api.onrender.com/trabajos/123/imagenes/456/download"
     * - "https://otro.com/imagen.jpg"         → "https://otro.com/imagen.jpg" (sin cambios)
     */
    fun absoluteUrl(path: String): String {
        // Si ya es absoluta (tiene protocolo), devolverla sin cambios
        if (path.startsWith("http://") || path.startsWith("https://")) return path

        // Asegurar que la ruta empiece con "/"
        val normalized = if (path.startsWith("/")) path else "/$path"

        // Concatenar BASE_URL + path
        return BASE_URL_NO_SLASH + normalized
    }
}
