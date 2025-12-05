package dev.luisbaena.prodentclient.presentation.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.luisbaena.prodentclient.ui.theme.ProdentGreenDarkContainer

 /**
  * Componente de cabecera reutilizable con opciones para mostrar icono de menú o de volver
  * y acciones adicionales en la parte derecha de la cabecera, como un icono de actualizar o similar.
  * La cabecera adapta su color de fondo según el tema (claro u oscuro).
  */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Cabecera(
    titulo: String,
    modifier: Modifier = Modifier,
    showMenuIcon: Boolean = false,
    showBackIcon: Boolean = false,
    onMenuClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}, // Acciones adicionales en la cabecera icono a la derecha ej para actualizar
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = if (isSystemInDarkTheme()) ProdentGreenDarkContainer else MaterialTheme.colorScheme.primary,
        titleContentColor = Color(0xFFFFFFFF),  // Siempre blanco
        navigationIconContentColor = Color(0xFFFFFFFF)  // Siempre blanco
    )
) {
    val iconColor = Color(0xFFFFFFFF)  // Siempre blanco para máximo contraste

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono de navegación (menú o back)
                when {
                    showMenuIcon -> {
                        IconButton(onClick = onMenuClick) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menú",
                                tint = iconColor,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                    showBackIcon -> {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = iconColor,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }

                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = iconColor
                )
            }
        },
        actions = actions,
        modifier = modifier,
        colors = colors
    )
}
