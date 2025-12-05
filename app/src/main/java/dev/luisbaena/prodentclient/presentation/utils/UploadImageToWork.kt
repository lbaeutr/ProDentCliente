package dev.luisbaena.prodentclient.presentation.utils

import android.net.Uri
import dev.luisbaena.prodentclient.presentation.viewmodel.WorkViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull

/**
 * Funcion para subir una imagen a un trabajo
 * comprime la imagen si es mayor a 1MB
 * y luego la sube usando el WorkViewModel
 */
fun uploadImageToWork(
    context: android.content.Context,
    workId: String,
    imageUri: Uri,
    imageType: String,
    description: String?,
    viewModel: WorkViewModel
) {
    try {
        // Obtener el archivo real del Uri
        val inputStream = context.contentResolver.openInputStream(imageUri) ?: return

        // Leer bytes del archivo
        val originalBytes = inputStream.readBytes()
        inputStream.close()

        // Comprimir imagen si es muy grande (mayor a 1MB)
        val finalBytes = if (originalBytes.size > 1024 * 1024) {
            // Decodificar bitmap
            val bitmap =
                android.graphics.BitmapFactory.decodeByteArray(originalBytes, 0, originalBytes.size)

            if (bitmap != null) {
                // Calcular nuevo tamaño (máximo 1920px en el lado más largo)
                val maxSize = 1920
                val scale = if (bitmap.width > bitmap.height) {
                    maxSize.toFloat() / bitmap.width
                } else {
                    maxSize.toFloat() / bitmap.height
                }

                val newWidth = (bitmap.width * scale).toInt()
                val newHeight = (bitmap.height * scale).toInt()

                // Redimensionar
                val scaledBitmap = android.graphics.Bitmap.createScaledBitmap(
                    bitmap,
                    newWidth,
                    newHeight,
                    true
                )

                // Comprimir a JPEG con calidad 80%
                val outputStream = java.io.ByteArrayOutputStream()
                scaledBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, outputStream)
                val compressedBytes = outputStream.toByteArray()

                bitmap.recycle()
                scaledBitmap.recycle()

                compressedBytes
            } else {
                originalBytes
            }
        } else {
            originalBytes
        }

        if (finalBytes.isNotEmpty()) {
            // Obtener MIME type
            val mimeType = context.contentResolver.getType(imageUri) ?: "image/jpeg"

            // Crear RequestBody para la imagen
            val requestFile = okhttp3.RequestBody.create(
                mimeType.toMediaTypeOrNull(),
                finalBytes
            )

            // Crear MultipartBody.Part
            val fileName = "image_${System.currentTimeMillis()}.jpg"
            val imagePart = okhttp3.MultipartBody.Part.createFormData(
                "file",
                fileName,
                requestFile
            )

            // Subir imagen
            viewModel.uploadImage(
                workId = workId,
                imagePart = imagePart,
                imageType = imageType,
                description = description,
                onSuccess = { }
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}