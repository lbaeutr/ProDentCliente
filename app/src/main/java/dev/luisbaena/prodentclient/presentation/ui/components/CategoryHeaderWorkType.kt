package dev.luisbaena.prodentclient.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.luisbaena.prodentclient.domain.model.WorkTypeCategory


    /**
     * Componente de cabecera de categoría de tipo de trabajo
     * muestra un icono y el nombre de la categoría, ademas de un divisor horizontal para separar visualmente
     * las secciones de la UI, funciona como un header dentro de listas o formularios
     */

@Composable
fun CategoryHeaderWorkType(categoria: String) {
    val categoryEnum = WorkTypeCategory.fromValue(categoria)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = categoryEnum?.displayName ?: categoria,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 4.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
    )
}