package dev.luisbaena.prodentclient.presentation.ui.components.common.inputs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Dropdown personalizado con la misma estética que CustomTextField
 *
 * Este componente es genérico y puede manejar cualquier tipo de dato T.
 * Permite mostrar un TextField que al ser clickeado despliega un menú
 * con una lista de opciones para seleccionar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> CustomDropdown( // Genérico para cualquier tipo de dato
    value: String, // Texto mostrado en el TextField
    label: String,
    placeholder: String = "",
    items: List<T>,// Lista de items del dropdown
    onItemSelected: (T) -> Unit, // Callback al seleccionar un item
    itemToString: (T) -> String, // Función para convertir item a String
    isLoading: Boolean = false, // Estado de carga
    isError: Boolean = false, // Estado de error
    errorMessage: String? = null, // Mensaje de error opcional
    emptyMessage: String = "No hay opciones disponibles",
    leadingIcon: ImageVector? = null, // Ícono opcional al inicio
    enabled: Boolean = true, // Estado habilitado/deshabilitado
    modifier: Modifier = Modifier, // Modificador para personalización externa
    onRetry: (() -> Unit)? = null, // Callback para reintentar carga en caso de lista vacía
    itemContent: @Composable ((T) -> Unit)? = null, // Contenido personalizado para cada item
    allowClear : Boolean = false, // Permitir opción de limpiar selección
    onClear : (() -> Unit)? = null// Callback al limpiar selección
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (enabled && !isLoading) {
                expanded = !expanded
            }
        },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            leadingIcon = if (leadingIcon != null) {
                {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = if (enabled) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        }
                    )
                }
            } else null,
            trailingIcon = {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.38f),
                errorBorderColor = MaterialTheme.colorScheme.error
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            isError = isError,
            supportingText = if (isError && errorMessage != null) {
                { Text(errorMessage) }
            } else null,
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            when {
                // Estado de carga
                isLoading -> {
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Cargando...")
                            }
                        },
                        onClick = {},
                        enabled = false
                    )
                }

                // Lista vacía
                items.isEmpty() -> {
                    DropdownMenuItem(
                        text = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = emptyMessage,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )

                                // Botón de reintentar si hay callback
                                if (onRetry != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TextButton(onClick = {
                                        expanded = false
                                        onRetry()
                                    }) {
                                        Text("Reintentar")
                                    }
                                }
                            }
                        },
                        onClick = {},
                        enabled = false
                    )
                }

                // Items disponibles
                else -> {
                    items.forEach { item ->
                        DropdownMenuItem(
                            text = {
                                if (itemContent != null) {
                                    itemContent(item)
                                } else {
                                    Text(
                                        text = itemToString(item),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            },
                            onClick = {
                                onItemSelected(item)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}