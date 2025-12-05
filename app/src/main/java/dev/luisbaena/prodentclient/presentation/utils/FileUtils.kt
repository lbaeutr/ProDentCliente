package dev.luisbaena.prodentclient.presentation.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

    /**
     * Utilidades para manejo de archivos e imágenes.
     *
     * PROPÓSITO:
     * Convertir URIs (de la galería o cámara) en archivos File temporales
     * para poder subirlos al servidor como MultipartBody.Part.
     *
     * PROBLEMA QUE RESUELVE:
     * Cuando el usuario selecciona una imagen de la galería o toma una foto,
     * Android devuelve un URI (ej: content://media/external/images/media/123).
     * Pero Retrofit necesita un File para crear MultipartBody.Part y subirlo al servidor.
     *
     * USO TÍPICO:
     * // 1. Usuario selecciona imagen de la galería
     * val imageUri: Uri = ... // content://media/external/images/media/123
     *
     * // 2. Convertir URI a File temporal
     * val file = imageUri.toFile(context)
     *
     * // 3. Crear RequestBody para Retrofit
     * val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
     * val imagePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
     *
     * // 4. Subir al servidor
     * workViewModel.uploadImage(workId, imagePart, ...)
     *
     * IMPORTANTE:
     * - Los archivos se crean en el CACHE (se limpian automáticamente por el sistema)
     * - Cada archivo tiene un nombre único con timestamp para evitar conflictos
     * - El archivo original NO se modifica, solo se copia
     *
     * Extensión que convierte un URI en un archivo File temporal.
     *
     * FLUJO:
     * 1. Abre un InputStream desde el URI (accede al contenido)
     * 2. Crea un archivo temporal en el directorio cache de la app
     * 3. Copia los bytes del InputStream al archivo temporal
     * 4. Cierra los streams y devuelve el archivo
     *
     * CASOS DE USO EN PRODENT:
     * - Subir imágenes de trabajos (CreateWorkScreen, WorkDetailScreen)
     * - Subir fotos de documentos
     * - Adjuntar imágenes a registros
     *
     * EJEMPLO DE NOMBRES GENERADOS:
     * - temp_image_1700000000123.jpg
     * - temp_image_1700000001456.jpg
     * - temp_image_1700000002789.jpg
     *
     * UBICACIÓN DE LOS ARCHIVOS:
     * /data/data/dev.luisbaena.prodentclient/cache/temp_image_*.jpg
     * (Android limpia automáticamente cuando la memoria es baja)
     */
fun Uri.toFile(context: Context): File? {
    return try {
        // 1. Abrir stream de lectura desde el URI
        val inputStream = context.contentResolver.openInputStream(this) ?: return null

        // 2. Crear archivo temporal en cache con timestamp único
        val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")

        // 3. Abrir stream de escritura al archivo temporal
        val outputStream = FileOutputStream(file)

        // 4. Copiar bytes del URI al archivo temporal
        inputStream.copyTo(outputStream)

        // 5. Cerrar streams para liberar recursos
        inputStream.close()
        outputStream.close()

        // 6. Devolver el archivo temporal
        file
    } catch (e: Exception) {
        // Si falla (permisos, memoria, etc.), imprimir error y devolver null
        e.printStackTrace()
        null
    }
}