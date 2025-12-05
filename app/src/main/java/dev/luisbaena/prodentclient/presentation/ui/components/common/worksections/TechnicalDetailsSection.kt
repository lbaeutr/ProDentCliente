package dev.luisbaena.prodentclient.presentation.ui.components.common.worksections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.luisbaena.prodentclient.data.remote.dto.work.WorkDetailDto
import dev.luisbaena.prodentclient.domain.model.WorkTypeCategory
import dev.luisbaena.prodentclient.presentation.ui.screens.work.DetailRow


    /**
     *  Componente de detalles técnicos del trabajo
     *  muestra categoría, descripción, piezas dentales, color, guía de color y material
     */
@Composable
fun TechnicalDetailsSection(work: WorkDetailDto) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Detalles Técnicos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Categoría
            DetailRow(
                icon = Icons.Default.Category,
                label = "Categoría",
                value = WorkTypeCategory.getDisplayName(work.tipoTrabajoCategoria)
            )

            // Descripción del trabajo
            if (!work.descripcionTrabajo.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                DetailRow(
                    icon = Icons.Default.Description,
                    label = "Descripción",
                    value = work.descripcionTrabajo
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Piezas dentales (chips)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Medication,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Piezas dentales:",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Chips de piezas
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val piezas = work.piezasDentales.split(",").map { it.trim() }
                items(piezas) { pieza ->
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = pieza,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            // Color
            if (!work.color.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                DetailRow(
                    icon = Icons.Default.Palette,
                    label = "Color",
                    value = work.color
                )
            }

            // Guía de color
            if (!work.guiaColor.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow(
                    icon = Icons.Default.ColorLens,
                    label = "Guía de color",
                    value = work.guiaColor
                )
            }

            // Material
            if (!work.materialNombre.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                DetailRow(
                    icon = Icons.Default.Build,
                    label = "Material",
                    value = work.materialNombre
                )
            }
        }
    }
}