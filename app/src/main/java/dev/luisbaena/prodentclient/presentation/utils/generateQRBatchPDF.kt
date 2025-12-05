package dev.luisbaena.prodentclient.presentation.utils

import android.graphics.Bitmap
import android.os.Environment
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import dev.luisbaena.prodentclient.presentation.ui.screens.qr.generateQRBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


    /**
     * Genera un PDF con códigos QR en lote
     * Pair<String, Int> donde String es la ruta del PDF e Int es la cantidad de QRs generados
     */
 suspend fun generateQRBatchPDF(
    start: Int,
    end: Int
): Pair<String, Int> = withContext(Dispatchers.IO) {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val fileName = "Lista_Qr_desde_${start}_hasta_${end}_$timestamp.pdf"

    val downloadsDir =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = File(downloadsDir, fileName)

    val writer = PdfWriter(file)
    val pdfDocument = PdfDocument(writer)
    val document = Document(pdfDocument, PageSize.A4)

    // Márgenes generosos para evitar que se corten los QR (10mm = 28.35 puntos)
    val marginMM = 10f
    val marginPoints = marginMM * 72f / 25.4f
    document.setMargins(marginPoints, marginPoints, marginPoints, marginPoints)

    // Tamaño exacto de 15mm (convertido a puntos: 15mm = 42.52 puntos)
    val qrSizeMM = 15f
    val qrSizePoints =
        qrSizeMM * 72f / 25.4f // Conversión de mm a puntos (1 inch = 72 points = 25.4mm)

    // Padding de celda
    val cellPadding = 3f

    // Calcular cuántas columnas caben en A4 con los márgenes aplicados
    val pageWidthPoints =
        PageSize.A4.width - (2 * marginPoints) // Ancho menos márgenes izquierdo y derecho

    // El ancho total de cada celda incluye el QR + padding (izquierdo + derecho)
    val totalCellWidth = qrSizePoints + (2 * cellPadding)
    val columnsPerPage = (pageWidthPoints / totalCellWidth).toInt()

    // Crear tabla con anchos absolutos en lugar de porcentajes para mayor precisión
    val table = Table(columnsPerPage)
        .useAllAvailableWidth()
        .setHorizontalAlignment(HorizontalAlignment.CENTER)

    var count = 0
    for (i in start..end) {
        val qrContent = "$i"
        val qrBitmap = generateQRBitmap(qrContent, 250)

        // Convertir bitmap a bytes para iText
        val stream = ByteArrayOutputStream()
        qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val imageData = ImageDataFactory.create(stream.toByteArray())
        val image = com.itextpdf.layout.element.Image(imageData)
            .setWidth(qrSizePoints)
            .setHeight(qrSizePoints)

        // Celda con QR y texto (ajustada al tamaño exacto)
        val cell = Cell()
            .add(image)
            .add(Paragraph(qrContent).setTextAlignment(TextAlignment.CENTER).setFontSize(7f))
            .setPadding(cellPadding)
            .setTextAlignment(TextAlignment.CENTER)

        table.addCell(cell)
        count++
    }

    // Rellenar celdas vacías si es necesario para completar la última fila
    val remainder = count % columnsPerPage
    if (remainder != 0) {
        repeat(columnsPerPage - remainder) {
            table.addCell(Cell().setBorder(null).setPadding(cellPadding))
        }
    }

    // Agregar la tabla completa (fluirá automáticamente en múltiples páginas)
    document.add(table)

    document.close()

    Pair(file.absolutePath, count)
}