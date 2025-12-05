package dev.luisbaena.prodentclient.presentation.ui.components.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.luisbaena.prodentclient.data.remote.dto.workType.WorkTypeDetailDto
import dev.luisbaena.prodentclient.domain.model.WorkTypeCategory
import dev.luisbaena.prodentclient.presentation.ui.components.common.buttons.PrimaryLoadingButton
import dev.luisbaena.prodentclient.presentation.ui.components.common.buttons.SecondaryButton
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.InfoCard_WorkTypeDetail
import dev.luisbaena.prodentclient.presentation.utils.formatDate
import java.util.Locale


   /**
     * Componente creado para mostrar el contenido detallado de un tipo de trabajo tiene un cabecero con el nombre, categoría e estado.....
     * muestra toda la información relevante del tipo de trabajo, incluyendo su nombre, categoría, estado, precio base, descripción y fecha de creación
     * además incluye botones para editar el tipo de trabajo o volver a la pantalla anterior
     */

@Composable
fun WorkTypeDetailContent(
    workType: WorkTypeDetailDto,
    scrollState: androidx.compose.foundation.ScrollState,
    navController: NavHostController
) {
    val categoryEnum = WorkTypeCategory.fromValue(workType.categoria)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // HEADER: Icono de categoría y nombre
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Nombre
                Text(
                    text = workType.nombre,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Categoría
                Text(
                    text = categoryEnum?.displayName ?: workType.categoria,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Badge de estado
                Surface(
                    color = if (workType.activo)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (workType.activo) "ACTIVO" else "INACTIVO",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (workType.activo)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onError,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // INFORMACIÓN PRINCIPAL
        Text(
            text = "Información",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Precio Base
        InfoCard_WorkTypeDetail(
            icon = Icons.Default.Euro,
            label = "Precio Base",
            value = workType.precioBase?.let { String.format(Locale.getDefault(), "%.2f€", it) }
                ?: "No especificado"
        )

        // Descripción
        if (!workType.descripcion.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            InfoCard_WorkTypeDetail(
                icon = Icons.Default.Description,
                label = "Descripción",
                value = workType.descripcion
            )
        }

        Spacer(modifier = Modifier.height(24.dp))


        // INFORMACIÓN ADICIONAL
        Text(
            text = "Información Adicional",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Fecha de creación
        InfoCard_WorkTypeDetail(
            icon = Icons.Default.CalendarToday,
            label = "Fecha de Creación",
            value = formatDate(workType.fechaCreacion)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // BOTÓN EDITAR
        PrimaryLoadingButton(
            text = "Editar Tipo de Trabajo",
            isLoading = false,
            onClick = {
                navController.navigate("worktype_update/${workType.id}")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Boton volver
        SecondaryButton(
            text = "Volver",
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}