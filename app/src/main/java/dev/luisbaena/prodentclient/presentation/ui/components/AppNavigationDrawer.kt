package dev.luisbaena.prodentclient.presentation.ui.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.auth0.android.jwt.BuildConfig.VERSION_NAME
import dev.luisbaena.prodentclient.R
import dev.luisbaena.prodentclient.domain.model.User
import dev.luisbaena.prodentclient.presentation.ui.navigation.AdminSubSection
import dev.luisbaena.prodentclient.presentation.ui.navigation.DrawerSection
import dev.luisbaena.prodentclient.presentation.ui.navigation.UserRole
import dev.luisbaena.prodentclient.presentation.ui.navigation.getAdminItemsBySubSection
import dev.luisbaena.prodentclient.presentation.ui.navigation.getDrawerItemsBySection
import dev.luisbaena.prodentclient.presentation.viewmodel.AuthViewModel

/**
 * Navigation Drawer Simple de la aplicación
 */
@Composable
fun AppNavigationDrawer(
    navController: NavController,
    authViewModel: AuthViewModel,
    onCloseDrawer: () -> Unit = {},
    isDrawerOpen: Boolean = false,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val uiState by authViewModel.uiState.collectAsState()
    val user = uiState.user

    val userRole = when {
        user == null -> UserRole.USER
        user.role?.uppercase() == "ADMIN" -> UserRole.ADMIN
        user.role?.uppercase() == "USER" -> UserRole.USER
        else -> UserRole.USER
    }

    ModalDrawerSheet(
        modifier = modifier,
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll( rememberScrollState())
        ) {
            DrawerHeader(user = user, onClose = onCloseDrawer)

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Obtener items agrupados por sección
            val itemsBySection = getDrawerItemsBySection(userRole)

            // Iterar por cada sección
            itemsBySection.forEach { (section, items) ->
                // Título de la sección
                Text(
                    text = section.title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(
                        top = 5.dp,
                        bottom = 5.dp,
                        start = 16.dp,
                        end = 16.dp
                    )
                )

                // Si es ADMINISTRACIÓN, renderizar con sub-secciones
                if (section == DrawerSection.ADMINISTRATION) {
                    val adminItemsBySubSection = getAdminItemsBySubSection(items)

                    adminItemsBySubSection.forEach { (subSection, subItems) ->
                        // Sub-título de la sub-sección
                        Text(
                            text = subSection.title,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(
                                top = 12.dp,
                                bottom = 4.dp,
                                start = 24.dp,
                                end = 16.dp
                            )
                        )

                        // Items de la sub-sección (con indentación)
                        subItems.forEach { item ->
                            val selected = currentRoute == item.route

                            DrawerItemCard(
                                icon = if (selected) item.selectedIcon else item.unselectedIcon,
                                title = item.title,
                                subtitle = item.subtitle ?: "",
                                isSelected = selected,
                                isIndented = true, // Nueva propiedad para indentar
                                onClick = {
                                    navController.navigate(item.route) {
                                        launchSingleTop = true
                                    }
                                    onCloseDrawer()
                                }
                            )
                        }
                    }
                } else {
                    // Para otras secciones, renderizado normal
                    items.forEach { item ->
                        val selected = currentRoute == item.route

                        DrawerItemCard(
                            icon = if (selected) item.selectedIcon else item.unselectedIcon,
                            title = item.title,
                            subtitle = item.subtitle ?: "",
                            isSelected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    launchSingleTop = true
                                }
                                onCloseDrawer()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 15.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Text(
                text = "© 2025 ProDent",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                   // .padding(bottom = 16.dp)
            )
            Text(
                text = "Versión ${VERSION_NAME}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp)
            )

        }
    }
}

/**
 * Header del Navigation Drawer
 * El cabecero incluye el nombre del usuario y un botón para cerrar el drawer
 *
 * Todo: Anadir foto de perfil del usuario para el futuro
 */
@Composable
private fun DrawerHeader(user: User?, onClose: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_splash_logo),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
        }

        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cerrar menú",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Card para un ítem del Navigation Drawer
 * que incluye soporte para items indentados (sub-items) que funciona de la siguiente manera:
 * - Si es indentado, se muestra un indicador visual a la izquierda
 * - El ícono es más pequeño
 * - El padding izquierdo es mayor
 * - El texto del título tiene un peso de fuente menor
 * - El tamaño del ícono es menor
 * - El resto del diseño se mantiene igual
 */
@Composable
private fun DrawerItemCard(
    icon: ImageVector,
    title: String,
    subtitle: String = "",
    isSelected: Boolean,
    isIndented: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = if (isIndented) 16.dp else 9.dp,
                vertical = 3.dp
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 1.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = if (isIndented) 8.dp else 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicador visual de sub-item (para items indentados)
            if (isIndented) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(24.dp)
                        .background(
                            color = if (isSelected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(2.dp)
                        )
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(if (isIndented) 20.dp else 24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                    ),
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}