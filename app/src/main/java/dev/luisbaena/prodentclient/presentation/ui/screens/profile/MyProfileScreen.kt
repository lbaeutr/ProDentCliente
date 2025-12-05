package dev.luisbaena.prodentclient.presentation.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.luisbaena.prodentclient.presentation.ui.components.BottomNavigationBar
import dev.luisbaena.prodentclient.presentation.ui.components.Cabecera
import dev.luisbaena.prodentclient.presentation.ui.components.ProfileHeader
import dev.luisbaena.prodentclient.presentation.ui.components.common.buttons.PrimaryLoadingButton
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.ProfileActionCard
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.ProfileCustomCard
import dev.luisbaena.prodentclient.presentation.ui.components.common.dialogs.ConfirmationDialog
import dev.luisbaena.prodentclient.presentation.ui.navigation.Routes
import dev.luisbaena.prodentclient.presentation.viewmodel.AuthViewModel

   /**
     * Pantalla de perfil del usuario
     * Muestra la información personal y opciones para editar perfil, cambiar contraseña y cerrar sesión
     */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileScreen(
    navController: NavController,
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    onOpenDrawer: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()
    val user = uiState.user

    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }

    // Refresca el perfil al entrar
    LaunchedEffect(Unit) {
        authViewModel.refreshProfile()
    }

    Scaffold(
        topBar = {
            Cabecera(
                titulo = "Mi Perfil",
                showMenuIcon = true,
                onMenuClick = onOpenDrawer
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->

        // LOADING STATE
        if (uiState.isLoading && user == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Text(
                        text = "Cargando perfil...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        // ERROR STATE
        else if (uiState.errorMessage != null && user == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        text = "⚠️",
                        style = MaterialTheme.typography.displayMedium
                    )
                    Text(
                        text = uiState.errorMessage ?: "Error al cargar perfil",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { authViewModel.refreshProfile() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Reintentar")
                    }
                }
            }
        }
        // SUCCESS STATE - MOSTRAR PERFIL
        else if (user != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header con avatar y nombre
                ProfileHeader(
                    nombre = user.nombre,
                    apellido = user.apellido,
                    role = user.role
                )

                Spacer(modifier = Modifier.height(24.dp))

                // INFORMACIÓN PERSONAL
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "INFORMACIÓN PERSONAL",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    ProfileCustomCard(
                        icon = Icons.Default.Person,
                        label = "Nombre completo",
                        value = user.getFullName()
                    )

                    ProfileCustomCard(
                        icon = Icons.Default.Email,
                        label = "Correo electrónico",
                        value = user.email
                    )

                    ProfileCustomCard(
                        icon = Icons.Default.Phone,
                        label = "Teléfono",
                        value = user.telefono
                    )

                }

                Spacer(modifier = Modifier.height(32.dp))

                // OPCIONES DE CUENTA
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "CUENTA",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    ProfileActionCard(
                        icon = Icons.Default.Edit,
                        title = "Editar perfil",
                        subtitle = "Actualiza tu información",
                        onClick = {
                            navController.navigate(Routes.EditProfile)
                        }
                    )

                    ProfileActionCard(
                        icon = Icons.Default.Lock,
                        title = "Cambiar contraseña",
                        subtitle = "Actualiza tu contraseña",
                        onClick = {
                            navController.navigate(Routes.ChangePassword)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // BOTÓN DE CERRAR SESIÓN
                PrimaryLoadingButton(
                    text = "Cerrar Sesión",
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    isLoading = uiState.isLoading,
                    loadingText = "Cerrando Sesión",
                    icon = Icons.AutoMirrored.Filled.Logout,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

            }
        }
    }
    // DIÁLOGO DE CONFIRMACIÓN DE LOGOUT
    ConfirmationDialog(
        show = showLogoutDialog,
        title = "¿Cerrar sesión?",
        message = "¿Estás seguro que deseas cerrar sesión?",
        onConfirm = {
            showLogoutDialog = false
            onLogout()
        },
        onDismiss = { showLogoutDialog = false },
        icon = Icons.AutoMirrored.Filled.Logout,
        confirmButtonText = "Cerrar sesión",
        dismissButtonText = "Cancelar"
    )
}