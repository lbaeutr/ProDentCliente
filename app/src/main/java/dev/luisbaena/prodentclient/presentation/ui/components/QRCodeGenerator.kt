package dev.luisbaena.prodentclient.presentation.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

    /**
     * Componente para generar y mostrar un código QR
     *
     * Funciona generando un Bitmap del código QR a partir del contenido proporcionado
     * y mostrándolo dentro de un Box con fondo blanco y padding.
     *
     * Contenido: String que se codificará en el código QR.
     * Modifier: Modificador para personalizar el diseño del componente.
     * Size: Tamaño del código QR en Dp (predeterminado a 200.dp).
     *
     * Se encuentra en fase de pruebas y puede ser mejorado en futuras versiones, dependiendo si la empresa da el salto al papel cero.
     */
@Composable
fun QRCodeGenerator(
    content: String,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp
) {
    var qrBitmap by remember(content) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(content) {
        if (content.isNotEmpty()) {
            qrBitmap = generateQRCode(content, size.value.toInt())
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .background(Color.White)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        qrBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Código QR",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Genera un Bitmap con el código QR
 */
private fun generateQRCode(content: String, size: Int): Bitmap? {
    return try {
        val hints = hashMapOf<EncodeHintType, Any>().apply {
            put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
            put(EncodeHintType.MARGIN, 1)
        }

        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)

        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(
                    x,
                    y,
                    if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                )
            }
        }

        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

