package dev.luisbaena.prodentclient.presentation.ui.components


import androidx.compose.animation.*
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.luisbaena.prodentclient.presentation.ui.navigation.bottomNavItems


    /**
     * Barra de Navegación Inferior
     * Muestra los íconos de navegación principales y maneja la lógica de navegación.
     */
@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier,
    visible: Boolean = true
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

        // Animaciones de entrada y salida para la barra de navegación
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }), // Desde abajo hacia arriba
        exit = slideOutVertically(targetOffsetY = { it }) // Desde arriba hacia abajo
    ) {
        NavigationBar(
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 8.dp
        ) {
            bottomNavItems.forEach { item ->
                val selected = currentRoute == item.route

                // Elemento de la barra de navegación que incluye ícono, etiqueta y lógica de navegación
                NavigationBarItem(
                    icon = {
                        BadgedBox(
                            badge = {
                                if (item.badgeCount != null && item.badgeCount > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.error
                                    ) {
                                        Text(
                                            text = item.badgeCount.toString(),
                                            color = MaterialTheme.colorScheme.onError
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    },
                    selected = selected,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                // Evitar acumulación -> Se me quedaba atascado en ciertos destinos
                                navController.graph.startDestinationRoute?.let { startRoute ->
                                    popUpTo(startRoute) {
                                        saveState = true
                                    }
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}