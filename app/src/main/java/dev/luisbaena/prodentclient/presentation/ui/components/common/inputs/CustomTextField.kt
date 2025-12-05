package dev.luisbaena.prodentclient.presentation.ui.components.common.inputs

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.luisbaena.prodentclient.presentation.ui.components.QRCodeScanner

   /**
     * Componente de campo de texto personalizado reutilizable
     */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: ImageVector? = null,
    isError: Boolean? = null,
    errorMessage: String? = null,
    focusRequester: FocusRequester? = null,
    supportingText: @Composable (() -> Unit)? = null,
    suffix : @Composable (() -> Unit)? = null,
    // Parámetros para QR Scanner
    enableQRScanner: Boolean = false,
    onQRScanned: ((String) -> Unit)? = null,
    onQRError: ((String) -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = if (leadingIcon != null) {
            {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null
                )
            }
        } else null,
        placeholder = { Text(placeholder) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        modifier = if (focusRequester != null) {
            modifier.focusRequester(focusRequester)
        } else {
            modifier
        },
        enabled = enabled,
        singleLine = singleLine,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        trailingIcon = if (enableQRScanner && onQRScanned != null) {
            {
                QRCodeScanner(
                    onQRScanned = onQRScanned,
                    onError = onQRError ?: {}
                )
            }
        } else trailingIcon,
        isError = isError ?: false,
        supportingText = if (isError == true && errorMessage != null) {
            { Text(errorMessage) }
        } else null
    )
}
