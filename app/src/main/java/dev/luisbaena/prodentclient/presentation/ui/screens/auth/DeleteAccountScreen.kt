package dev.luisbaena.prodentclient.presentation.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.luisbaena.prodentclient.presentation.ui.components.Cabecera
import dev.luisbaena.prodentclient.presentation.ui.components.common.dialogs.ConfirmationDialog
import dev.luisbaena.prodentclient.presentation.ui.components.common.dialogs.SuccessDialog
import dev.luisbaena.prodentclient.presentation.viewmodel.AuthViewModel
import dev.luisbaena.prodentclient.presentation.ui.components.common.buttons.PrimaryLoadingButton
import dev.luisbaena.prodentclient.presentation.ui.components.common.buttons.SecondaryButton
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.AdverCard
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.ErrorCard
import dev.luisbaena.prodentclient.presentation.ui.components.common.inputs.CustomTextField


   /**
     * Pantalla para eliminar la cuenta de un usuario por correo electrónico
     * Muestra un campo para ingresar el correo, un botón para eliminar y diálogos de confirm
     */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAccountScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    onOpenDrawer: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()

    var emailToDelete by rememberSaveable { mutableStateOf("") }
    var showConfirmDialog by rememberSaveable { mutableStateOf(false) }
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Cabecera(
                titulo = "Eliminar Cuenta de Usuario",
                showBackIcon = true,
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            AdverCard(
                modifier = Modifier.padding(16.dp),
                title = "¡Atención!",
                message = "Esta acción es irreversible. Se eliminarán todos los datos del usuario permanentemente."
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo para ingresar el correo electrónico
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "ELIMINAR USUARIO",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Ingresa el correo electrónico del usuario que deseas eliminar:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Mensaje de error
                ErrorCard(
                    errorMessage = uiState.errorMessage ?: "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (uiState.errorMessage != null) {
                                Modifier
                            } else {
                                Modifier.height(0.dp)
                            }
                        )
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomTextField(
                    value = emailToDelete,
                    onValueChange = {
                        emailToDelete = it
                        authViewModel.clearError()
                    },
                    label = "Correo electrónico",
                    placeholder = "usuario@ejemplo.com",
                    keyboardType = KeyboardType.Email,
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = Icons.Default.Email,
                )
            }
            Spacer(modifier = Modifier.height(32.dp))

            // Botones de acción
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PrimaryLoadingButton(
                    text = "Eliminar usuario",
                    onClick = {
                        if (emailToDelete.trim().isNotEmpty()) {
                            showConfirmDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = emailToDelete.trim().isNotEmpty(),
                    isLoading = uiState.isLoading,
                    loadingText = "Eliminando usuario...",
                    icon = Icons.Default.DeleteForever,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                )
                Spacer(Modifier.height(8.dp))

                SecondaryButton(
                    text = "Cancelar",
                    onClick = { navController.popBackStack() },
                    enabled = !uiState.isLoading
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Diálogo de confirmación
    ConfirmationDialog(
        show = showConfirmDialog,
        title = "Confirmar eliminación",
        message = "¿Estás seguro de que deseas eliminar la cuenta del usuario con correo:\n\n$emailToDelete\n\nEsta acción no se puede deshacer.",
        onConfirm = {
            showConfirmDialog = false
            authViewModel.deleteAccountByEmail(emailToDelete.trim())
        },
        onDismiss = { showConfirmDialog = false },
        icon = Icons.Default.DeleteForever,
        confirmButtonText = "Sí, eliminar",
        dismissButtonText = "Cancelar"
    )

    // Diálogo de éxito
    SuccessDialog(
        show = showSuccessDialog,
        title = "Usuario eliminado",
        message = "La cuenta del usuario ha sido eliminada exitosamente.",
        onConfirm = {
            showSuccessDialog = false
            emailToDelete = ""
            authViewModel.clearError()
            navController.popBackStack()
        },
        icon = Icons.Default.CheckCircle,
        confirmButtonText = "Aceptar"
    )

    // Observar cuando la cuenta se elimina exitosamente
    LaunchedEffect(uiState.isAccountDeleted) {
        if (uiState.isAccountDeleted) {
            showSuccessDialog = true
            authViewModel.resetAccountDeletedState()
        }
    }
}