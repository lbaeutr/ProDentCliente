package dev.luisbaena.prodentclient.presentation.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.luisbaena.prodentclient.domain.model.WorkStatus

    /**
     * Devuelve el color asociado a un estado de trabajo
     * utilizando en las cards de trabajos de workScreen
     */

@Composable
 fun getStatusColor(estado: String): Color {
    val status = WorkStatus.fromValue(estado)

    return when (status) {
        // Estado inicial
        WorkStatus.PLASTER -> MaterialTheme.colorScheme.secondary

        // Estados de Metal
        WorkStatus.METAL_LAB -> MaterialTheme.colorScheme.tertiary
        WorkStatus.METAL_CAD -> MaterialTheme.colorScheme.tertiary
        WorkStatus.METAL_TICARE -> MaterialTheme.colorScheme.tertiary
        WorkStatus.METAL_CNC -> MaterialTheme.colorScheme.tertiary
        WorkStatus.METAL_IDEA -> MaterialTheme.colorScheme.tertiary

        // Estados de Resina
        WorkStatus.RESIN_LAB -> MaterialTheme.colorScheme.primary
        WorkStatus.RESIN_SH -> MaterialTheme.colorScheme.primary
        WorkStatus.RESIN_ORTOHOMAX -> MaterialTheme.colorScheme.primary

        // Estados de Clínica
        WorkStatus.CLINIC_CLIENT -> MaterialTheme.colorScheme.secondary
        WorkStatus.CLINIC_RODETES -> MaterialTheme.colorScheme.secondary
        WorkStatus.CLINIC_BIZCOCHO -> MaterialTheme.colorScheme.secondary
        WorkStatus.CLINIC_AESTHETIC -> MaterialTheme.colorScheme.secondary

        // Estado de Cerámica
        WorkStatus.CERAMIC -> MaterialTheme.colorScheme.secondary

        // Estados finales
        WorkStatus.CANCELLED -> MaterialTheme.colorScheme.error
        WorkStatus.FINISHED -> Color(0xFF4CAF50) // Verde para terminado
        WorkStatus.PAUSED -> MaterialTheme.colorScheme.error

        // Estados desconocidos o no mapeados
        null -> MaterialTheme.colorScheme.surfaceVariant
    }
}
