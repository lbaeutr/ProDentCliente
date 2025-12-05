package dev.luisbaena.prodentclient.presentation.ui.components.common.cards


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


/**
 * Banner de Fecha y Hora
 * Componente reutilizable que muestra la fecha y hora actual
 * con actualización automática cada segundo
 *
 * SIN USAR EN LA APP POR AHORA, PERO ME GUSTO LA IDEA PARA FUTURO
 */
@Composable
fun DateTimeCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f),
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    labelColor: Color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
    dividerColor: Color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
) {
    var currentTime by remember { mutableStateOf(getCurrentTime()) }
    var currentDate by remember { mutableStateOf(getCurrentDateLong()) }

    // Actualizar cada segundo
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            currentTime = getCurrentTime()
            currentDate = getCurrentDateLong()
        }
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Fecha
            Column {
                Text(
                    text = "FECHA",
                    style = MaterialTheme.typography.labelSmall,
                    color = labelColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = currentDate,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }

            // Divisor vertical
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp)
                    .background(dividerColor)
            )

            // Hora
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "HORA",
                    style = MaterialTheme.typography.labelSmall,
                    color = labelColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = currentTime,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}

/**
 * Obtiene la fecha en formato largo
 * Formato: "Lunes, 30 de Octubre"
 */
private fun getCurrentDateLong(): String {
    val dateFormat = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "ES"))
    return dateFormat.format(Date()).replaceFirstChar { it.uppercase() }
}

/**
 * Obtiene la hora actual
 * Formato: "14:35:22"
 */
private fun getCurrentTime(): String {
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return timeFormat.format(Date())
}