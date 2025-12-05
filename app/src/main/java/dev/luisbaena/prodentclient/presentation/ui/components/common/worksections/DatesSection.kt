package dev.luisbaena.prodentclient.presentation.ui.components.common.worksections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.luisbaena.prodentclient.data.remote.dto.work.WorkDetailDto
import dev.luisbaena.prodentclient.presentation.ui.screens.work.DetailRow
import dev.luisbaena.prodentclient.presentation.utils.formatDate


    /**
     * Componente: Sección de fechas del trabajo
     * usado en WorkDetailContent
     */
@Composable
fun DatesSection(work: WorkDetailDto) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Fechas",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Fecha de entrada
            DetailRow(
                icon = Icons.Default.CalendarToday,
                label = "Fecha entrada",
                value = formatDate(work.fechaEntrada)
            )

            // Fecha entrega estimada
            if (!work.fechaEntregaEstimada.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                DetailRow(
                    icon = Icons.Default.Event,
                    label = "Entrega estimada",
                    value = formatDate(work.fechaEntregaEstimada)
                )
            }

            // Fecha entrega real
            if (!work.fechaEntregaReal.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                DetailRow(
                    icon = Icons.Default.CheckCircle,
                    label = "Entrega real",
                    value = formatDate(work.fechaEntregaReal)
                )
            }
        }
    }
}