package dev.luisbaena.prodentclient.presentation.ui.components.common.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.luisbaena.prodentclient.data.remote.dto.workType.WorkTypeListDto
import dev.luisbaena.prodentclient.domain.model.WorkTypeCategory

  /**
    * Componente de Card para mostrar un tipo de trabajo en una lista , usado en la pantalla de tipos de trabajo
    * usado para mostrar los tipos de trabajo en listas, con su nombre, categoría, precio base y estado (activo/inactivo)
    *  para mostrar todos los tipos de trabajo disponibles
    */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkTypeCard(
    workType: WorkTypeListDto,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // esto sirve para convertir la categoría de String a enum y así obtener el nombre legible
           val categoryEnum = WorkTypeCategory.fromValue(workType.categoria)

            // CONTENIDO: Nombre, Categoría y Precio
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Nombre
                Text(
                    text = workType.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Categoría
                Text(
                    text = categoryEnum?.displayName ?: workType.categoria,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Precio (si existe)
                workType.precioBase?.let { precio ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${String.format("%.2f", precio)}€", // Formatear a 2 decimales
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // BADGE DE ESTADO
            Surface(
                color = if (workType.activo)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (workType.activo) "Activo" else "Inactivo",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (workType.activo)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)
                )
            }
        }
    }
}