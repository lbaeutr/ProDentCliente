package dev.luisbaena.prodentclient.presentation.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

    /**
     * Elementos de la barra de navegación inferior
     * Usados en qr y main screen
     */
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int? = null
) {
    object Main : BottomNavItem(
        route = Routes.Main,
        title = "Inicio",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    object Qr : BottomNavItem(
        route = Routes.QR,
        title = "QR",
        selectedIcon = Icons.Filled.QrCodeScanner,
        unselectedIcon = Icons.Outlined.QrCodeScanner
    )
}

// Lista para iterar
val bottomNavItems = listOf(
    BottomNavItem.Main,
    BottomNavItem.Qr,
    )