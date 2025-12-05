package dev.luisbaena.prodentclient.presentation.ui.components.common.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

    /**
     * Diálogo de éxito reutilizable
     * Usado en: ChangePasswordScreen, EditProfileScreen
     *
     * NO USADA Actualmente, pero SERA UTILIZADA en futuras actualizaciones
     */
@Composable
fun SuccessDialog(
    show: Boolean,
    title: String,
    message: String,
    onConfirm: () -> Unit,
    icon: ImageVector = Icons.Default.CheckCircle,
    iconTint: Color? = null,
    confirmButtonText: String = "Aceptar",
    confirmButtonColor: Color? = null
) {
    if (show) {
        AlertDialog(
            onDismissRequest = { },
            icon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint ?: MaterialTheme.colorScheme.primary
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
                        containerColor = confirmButtonColor ?: MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = confirmButtonText,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        )
    }
}

