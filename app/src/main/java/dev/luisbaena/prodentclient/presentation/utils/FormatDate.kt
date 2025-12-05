package dev.luisbaena.prodentclient.presentation.utils

import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Formatea fechas en formato ISO 8601 a un formato legible "dd/MM/yyyy HH:mm"
 * Si la fecha no puede ser parseada, devuelve el string original
 */
fun formatDate(isoDate: String): String {
    return try {
        // Formato de entrada ISO 8601
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
        // Formato de salida legible
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        // Limpiar el string de entrada (eliminar zona horaria si existe)
        val cleanDate = isoDate.substringBefore('[').substringBefore('+').substringBefore('Z')

        val date = inputFormat.parse(cleanDate)
        date?.let { outputFormat.format(it) } ?: isoDate
    } catch (_: Exception) {
        // Si falla el parseo, intentar con formato simple
        try {
            val simpleFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = simpleFormat.parse(isoDate)
            date?.let { outputFormat.format(it) } ?: isoDate
        } catch (_: Exception) {
            // Si todo falla, devolver el string original
            isoDate
        }
    }
}