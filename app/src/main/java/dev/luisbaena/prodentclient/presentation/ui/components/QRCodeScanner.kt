package dev.luisbaena.prodentclient.presentation.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

/**
 * Componente para escanear códigos QR
 *
 * Usa la librería ZXing a través de JourneyApps
 * funciona mediante un IconButton que lanza el escáner al ser presionado
 * Usado en: SearchScreen (para escanear códigos QR de trabajos) y en CreateWorkScreen (para escanear códigos QR de los papeles para setearlos automáticamente)
 */
@Composable
fun QRCodeScanner(
    onQRScanned: (String) -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.QrCodeScanner,
    iconTint: Color? = null,
    iconSize: Modifier = Modifier
) {
    val scanLauncher = rememberLauncherForActivityResult(
        contract = ScanContract()
    ) { result ->
        if (result.contents != null) {
            onQRScanned(result.contents)
        } else {
            onError("Escaneo cancelado")
        }
    }

    IconButton(
        onClick = {
            scanLauncher.launch(
                ScanOptions().apply {
                    setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                    setPrompt("Escanea el código QR del trabajo\nToca atrás para cancelar")
                    setBeepEnabled(true)
                    setOrientationLocked(true)
                    setCameraId(0) // Establece la camara trasera
                }
            )
        },
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Escanear código QR",
            tint = iconTint ?: MaterialTheme.colorScheme.primary,
            modifier = iconSize
        )
    }
}

