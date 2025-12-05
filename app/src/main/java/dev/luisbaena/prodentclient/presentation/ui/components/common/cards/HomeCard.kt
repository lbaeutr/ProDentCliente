package dev.luisbaena.prodentclient.presentation.ui.components.common.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.luisbaena.prodentclient.domain.model.WorkStatusStatistics
import dev.luisbaena.prodentclient.presentation.utils.getStatusColor

    /**
     * Tarjeta que muestra estadísticas de estado de trabajo
     */

@Composable
fun HomeCard(
    statistic: WorkStatusStatistics,
    //onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = getStatusColor(statistic.status.value)

    Card(
        modifier = modifier
            .height(120.dp),
        // .clickable(onClick = onClick), // ESTA COMENTADO PORQUE DE MOMENTO NO TIENE FUNCIONALIDAD, ESTA PUESTO PARA FUTURO, PARA OBTENER DETALLES AL HACER CLICK
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Indicador de color
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(statusColor)
            )

            // Contenido
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${statistic.count}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
                Text(
                    text = statistic.status.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}