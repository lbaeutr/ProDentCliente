package dev.luisbaena.prodentclient.presentation.ui.components.common

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.luisbaena.prodentclient.domain.model.Clinic
import dev.luisbaena.prodentclient.presentation.ui.components.HorarioRow
import dev.luisbaena.prodentclient.presentation.ui.components.common.buttons.PrimaryLoadingButton
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.AdverCard
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.ClinicDetailsCard

    /**
     * Contenido detallado de la clínica para pantallas de detalle
     * Muestra toda la información de la clínica incluyendo:
     * - Nombre y estado
     * - Información de contacto (dirección, teléfono, email)
     * - Horarios de atención
     * - Observaciones
     * - Botones de acción (editar, eliminar)
     */
@Composable
fun ClinicaDetailContent(
    clinica: Clinic,
    scrollState: ScrollState,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    showButtons: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        if (showButtons) {
            AdverCard(
                title = "Modo Administrador",
                message = "Advertencia: Estás en modo administrador. Ten cuidado al realizar cambios.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            )
        }

        // HEADER CON NOMBRE Y ESTADO
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = clinica.nombre,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // Badge de estado
                    Surface(
                        color = if (clinica.activa)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (clinica.activa) "Activa" else "Inactiva",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (clinica.activa)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onError,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        // INFORMACIÓN DE CONTACTO
        Text(
            text = "Información de contacto",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Usar el componente InfoCard modularizado
        ClinicDetailsCard(
            icon = Icons.Default.LocationOn,
            label = "Dirección",
            value = clinica.direccion
        )

        Spacer(modifier = Modifier.height(12.dp))

        ClinicDetailsCard(
            icon = Icons.Default.Phone,
            label = "Teléfono",
            value = clinica.telefono
        )

        Spacer(modifier = Modifier.height(12.dp))

        ClinicDetailsCard(
            icon = Icons.Default.Email,
            label = "Email",
            value = clinica.email
        )

        Spacer(modifier = Modifier.height(16.dp))


        // HORARIOS
        Text(
            text = "Horarios de atención",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                val horarios = clinica.horarios
                if (horarios?.hasHorarios() == true) {
                    // Usar la función toListPairs() para obtener los horarios
                    val listaPares = horarios.toListPairs()
                    listaPares.forEachIndexed { index, (dia, horario) ->
                        HorarioRow(dia, horario)
                        if (index < (listaPares.size - 1)) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                } else {
                    // Mostrar mensaje cuando no hay horarios
                    Text(
                        text = "No hay horarios registrados",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // OBSERVACIONES
        if (clinica.observaciones != null) {
            Text(
                text = "Observaciones",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = clinica.observaciones,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }


        // BOTONES DE ACCIÓN
        if (showButtons) {
            //boton actualizar
            PrimaryLoadingButton(
                text = "Actualizar Datos",
                onClick = onEditClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                icon =  Icons.Default.Edit,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}