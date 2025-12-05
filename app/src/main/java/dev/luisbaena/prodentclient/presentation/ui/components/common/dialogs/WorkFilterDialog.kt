package dev.luisbaena.prodentclient.presentation.ui.components.common.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.luisbaena.prodentclient.domain.model.WorkStatus

//TODO eliminar el filtro de urgente no funciona bien

/**
 * Diálogo para filtrar la lista de trabajos por estado y urgencia
 *  currentEstado Estado actualmente seleccionado (o null si no hay filtro)
 *  currentUrgente Valor actual del filtro de urgencia
 *  onApplyFilters Callback cuando se aplican los filtros, devuelve el estado seleccionado y si es urgente
 *  onDismiss Callback cuando se cierra el diálogo sin aplicar filtros
 */

@Composable
fun WorkFilterDialog(
    currentEstado: String?,
    currentUrgente: Boolean,
    onApplyFilters: (String?, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedEstado by remember { mutableStateOf(currentEstado) }
    var selectedUrgente by remember { mutableStateOf(currentUrgente) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                "Filtrar Trabajos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                // Botón limpiar filtros
                TextButton(
                    onClick = {
                        selectedEstado = null
                        selectedUrgente = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.ClearAll, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Limpiar Filtros")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Estado
                Text(
                    text = "Estado",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Radio buttons para estados
                WorkStatus.entries.forEach { status ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedEstado == status.value,
                            onClick = {
                                selectedEstado = if (selectedEstado == status.value) {
                                    null // Deseleccionar
                                } else {
                                    status.value
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = status.displayName,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.height(16.dp))

                // Urgente
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Solo urgentes",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Switch(
                        checked = selectedUrgente,
                        onCheckedChange = { selectedUrgente = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApplyFilters(selectedEstado, selectedUrgente)
                }
            ) {
                Text("Aplicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}