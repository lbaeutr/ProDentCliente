package dev.luisbaena.prodentclient.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

//  Colores de marca
val ColorScheme.prodentGreen: Color
    @Composable get() = ProdentGreen

val ColorScheme.prodentGray: Color
    @Composable get() = ProdentGray

//  Colores de negocio
val ColorScheme.dentistaColor: Color
    @Composable get() = DentistaColor

val ColorScheme.trabajoColor: Color
    @Composable get() = TrabajoColor

val ColorScheme.clinicaColor: Color
    @Composable get() = ClinicaColor

val ColorScheme.pacienteColor: Color
    @Composable get() = PacienteColor

//  Estados
val ColorScheme.success: Color
    @Composable get() = Success

val ColorScheme.warning: Color
    @Composable get() = Warning

val ColorScheme.info: Color
    @Composable get() = Info

val ColorScheme.statusPendiente: Color
    @Composable get() = StatusPendiente

val ColorScheme.statusEnProceso: Color
    @Composable get() = StatusEnProceso

val ColorScheme.statusCompletado: Color
    @Composable get() = StatusCompletado

val ColorScheme.statusCancelado: Color
    @Composable get() = StatusCancelado

//  Helper para obtener color según estado
@Composable
fun ColorScheme.getStatusColor(status: String): Color {
    return when (status.lowercase()) {
        "pendiente" -> statusPendiente
        "en_proceso", "en proceso" -> statusEnProceso
        "completado", "finalizado" -> statusCompletado
        "cancelado" -> statusCancelado
        else -> onSurfaceVariant
    }
}

//  Helper para obtener color según tipo
@Composable
fun ColorScheme.getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "dentista" -> dentistaColor
        "trabajo" -> trabajoColor
        "clinica" -> clinicaColor
        "paciente" -> pacienteColor
        else -> primary
    }
}
