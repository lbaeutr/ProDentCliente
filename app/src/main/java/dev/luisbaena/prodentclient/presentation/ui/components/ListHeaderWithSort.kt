package dev.luisbaena.prodentclient.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Opción de ordenamiento genérica
 *
 *  label Texto que se muestra (ej: "Nombre A-Z")
 *  value Valor identificador único (ej: "name_asc")
 */
data class SortOption(
    val label: String,
    val value: String
)

/**
 * Header de lista con contador y menú de ordenamiento
 * Componente genérico y reutilizable para cualquier lista ordenable
 *
 * itemCount Cantidad de items en la lista
 * itemLabel Etiqueta singular del item (ej: "usuario", "cita", "producto")
 * itemLabelPlural Etiqueta plural del item (ej: "usuarios", "citas", "productos"). Si es null, se añade 's' al singular
 * sortOptions Lista de opciones de ordenamiento
 * currentSortValue Valor de ordenamiento actual seleccionado
 * onSortSelected Callback cuando se selecciona un ordenamiento
 *
 * TODO: COMPONENTE  CON POTENCIAL IMPLEMENTACION EN EL FUTURO.
 */
@Composable
fun ListHeaderWithSort(
    itemCount: Int,
    itemLabel: String,
    itemLabelPlural: String? = null,
    sortOptions: List<SortOption>,
    currentSortValue: String,
    onSortSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSortMenu by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Contador de items
        Text(
            text = buildCounterText(itemCount, itemLabel, itemLabelPlural),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Botón de ordenamiento con menú
        Box {
            TextButton(onClick = { showSortMenu = true }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Sort,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ordenar")
            }

            // Menú desplegable
            DropdownMenu(
                expanded = showSortMenu,
                onDismissRequest = { showSortMenu = false }
            ) {
                sortOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.label) },
                        onClick = {
                            onSortSelected(option.value)
                            showSortMenu = false
                        },
                        leadingIcon = {
                            if (currentSortValue == option.value) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

/**
 * Construye el texto del contador de forma inteligente
 */
private fun buildCounterText(
    count: Int,
    singular: String,
    plural: String?
): String {
    return if (count == 1) {
        "$count $singular"
    } else {
        "$count ${plural ?: "${singular}s"}"
    }
}