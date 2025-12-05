package dev.luisbaena.prodentclient.presentation.ui.components.common.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

    /**
     * Diálogo de confirmación reutilizable
     * Usado en: MyProfileScreen (logout), y otras acciones que requieran confirmación...
     */
@Composable
fun ConfirmationDialog(
    show: Boolean,
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    icon: ImageVector = Icons.AutoMirrored.Filled.Logout,
    iconTint: Color? = null,
    confirmButtonText: String = "Confirmar",
    dismissButtonText: String = "Cancelar",
    confirmButtonColor: Color? = null
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint ?: MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = confirmButtonColor ?: MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(
                        text = confirmButtonText,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = dismissButtonText,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        )
    }
}

