package dev.luisbaena.prodentclient.presentation.ui.components.common.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


 /**
  * Diálogo que muestra el progreso de la subida de imágenes
  *
  * uploadedCount Número de imágenes subidas hasta el momento
  * totalCount Número total de imágenes a subir
  * error Mensaje de error en caso de que la subida falle
  * onDismiss Acción a realizar al cerrar el diálogo

  */
@Composable
 fun ImageUploadProgressDialog(
    uploadedCount: Int,
    totalCount: Int,
    error: String?,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            if (uploadedCount >= totalCount || error != null) {
                onDismiss()
            }
        },
        icon = {
            if (error != null) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp)
                )
            }
        },
        title = {
            Text(
                text = if (error != null) "Error al subir imágenes"
                else if (uploadedCount >= totalCount) "¡Imágenes subidas!"
                else "Subiendo imágenes...",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (error != null) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Text(
                        text = "Subiendo imagen $uploadedCount de $totalCount",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Progress bar
                    LinearProgressIndicator(
                        progress = uploadedCount.toFloat() / totalCount.toFloat(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${((uploadedCount.toFloat() / totalCount.toFloat()) * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            if (uploadedCount >= totalCount || error != null) {
                TextButton(onClick = onDismiss) {
                    Text("Aceptar", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = if (error != null) {
            {
                TextButton(
                    onClick = {
                        // TODO: Reintentar subida
                        onDismiss()
                    }
                ) {
                    Text("Reintentar")
                }
            }
        } else null
    )
}