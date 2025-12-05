package dev.luisbaena.prodentclient.presentation.ui.screens.qr

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Environment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
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
import dev.luisbaena.prodentclient.presentation.ui.components.BottomNavigationBar
import dev.luisbaena.prodentclient.presentation.ui.components.Cabecera
import dev.luisbaena.prodentclient.presentation.ui.components.common.buttons.PrimaryLoadingButton
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.ErrorCard
import dev.luisbaena.prodentclient.presentation.ui.components.common.inputs.CustomTextField
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.graphics.set
import androidx.core.graphics.createBitmap
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.InfoCard
import dev.luisbaena.prodentclient.presentation.utils.generateQRBatchPDF

/**
 * Pantalla para generar códigos QR en lote y exportarlos a PDF
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrGeneratorScreen(
    navController: NavHostController,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Estados del formulario
    var numeroInicio by rememberSaveable { mutableStateOf("") }
    var numeroFin by rememberSaveable { mutableStateOf("") }

    // Estados de validación
    var numeroInicioError by rememberSaveable { mutableStateOf(false) }
    var numeroFinError by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    // Estados de proceso
    var isGenerating by rememberSaveable { mutableStateOf(false) }
    var generatedPdfPath by rememberSaveable { mutableStateOf<String?>(null) }
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }
    var totalQRs by rememberSaveable { mutableStateOf(0) }

    Scaffold(
        topBar = {
            Cabecera(
                titulo = "Generar Códigos QR",
                showMenuIcon = true,
                onMenuClick = onOpenDrawer,
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // ENCABEZADO CON ICONO
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Generador de QR en Lote",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Genera múltiples códigos QR en un PDF",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // CONFIGURACIÓN
            Text(
                text = "Configuración",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))


            // Número de inicio
            CustomTextField(
                value = numeroInicio,
                onValueChange = {
                    if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                        numeroInicio = it
                        numeroInicioError = false
                        errorMessage = null
                    }
                },
                label = "Número de Inicio *",
                placeholder = "Ej: 50",
                leadingIcon = Icons.Default.PlayArrow,
                keyboardType = KeyboardType.Number,
                isError = numeroInicioError,
                errorMessage = "El número de inicio es obligatorio",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Número final
            CustomTextField(
                value = numeroFin,
                onValueChange = {
                    if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                        numeroFin = it
                        numeroFinError = false
                        errorMessage = null
                    }
                },
                label = "Número Final *",
                placeholder = "Ej: 60",
                leadingIcon = Icons.Default.Stop,
                keyboardType = KeyboardType.Number,
                isError = numeroFinError,
                errorMessage = "El número final es obligatorio",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // INFO CARD
            InfoCard(
                message = "Los códigos QR generados contendrán números secuenciales desde el número de inicio hasta el número final proporcionados." +
                        " para obtimizacion poner lotes de 142 QR por hoja A4.",
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ERROR MESSAGE
            if (errorMessage != null) {
                ErrorCard(
                    errorMessage = errorMessage!!,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // BOTÓN GENERAR PDF
            PrimaryLoadingButton(
                text = "Generar PDF",
                isLoading = isGenerating,
                onClick = {
                    // Validar
                    numeroInicioError = numeroInicio.isBlank()
                    numeroFinError = numeroFin.isBlank()

                    if (!numeroInicioError && !numeroFinError) {
                        val inicio = numeroInicio.toInt()
                        val fin = numeroFin.toInt()

                        if (fin < inicio) {
                            errorMessage = "El número final debe ser mayor o igual al inicial"
                            return@PrimaryLoadingButton
                        }

                        // Generar PDF
                        scope.launch {
                            isGenerating = true
                            errorMessage = null
                            try {
                                val result = generateQRBatchPDF(
                                    start = inicio,
                                    end = fin
                                )
                                generatedPdfPath = result.first
                                totalQRs = result.second
                                showSuccessDialog = true
                            } catch (e: Exception) {
                                errorMessage = "Error al generar PDF: ${e.message}"
                            } finally {
                                isGenerating = false
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )


            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // DIÁLOGO DE ÉXITO
    if (showSuccessDialog && generatedPdfPath != null) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text("¡PDF Generado!")
            },
            text = {
                Column {
                    Text("El archivo PDF con los códigos QR se ha generado exitosamente.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Total: $totalQRs códigos QR",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        openPDF(context, generatedPdfPath!!)
                    }
                ) {
                    Icon(Icons.Default.FileOpen, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Abrir PDF")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                    }
                ) {
                    Text("Cerrar")
                }
            }
        )
    }
}

/**
 * Genera un bitmap de código QR, que es un formato de imagen que representa datos codificados en un patrón de puntos.
 */
 fun generateQRBitmap(content: String, size: Int): Bitmap {
    val hints = hashMapOf<EncodeHintType, Any>().apply {
        put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
        put(EncodeHintType.MARGIN, 1)
    }

    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)

    val width = bitMatrix.width
    val height = bitMatrix.height
    val bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)

    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
        }
    }

    return bitmap
}


/**
 * Abre el PDF generado
 */
private fun openPDF(context: Context, pdfPath: String) {
    val file = File(pdfPath)
    if (!file.exists()) return

    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
    }

    try {
        context.startActivity(Intent.createChooser(intent, "Abrir PDF con..."))
    } catch (@Suppress("SwallowedException") ignored: Exception) {
        // Evita crash si no hay app para abrir PDFs
    }
}


