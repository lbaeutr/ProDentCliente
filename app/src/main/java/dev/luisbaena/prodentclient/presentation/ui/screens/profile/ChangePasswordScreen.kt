package dev.luisbaena.prodentclient.presentation.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.luisbaena.prodentclient.presentation.ui.components.Cabecera
import dev.luisbaena.prodentclient.presentation.ui.components.common.inputs.PasswordTextField
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.ErrorCard
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.InfoCard
import dev.luisbaena.prodentclient.presentation.ui.components.common.buttons.PrimaryLoadingButton
import dev.luisbaena.prodentclient.presentation.ui.components.common.buttons.SecondaryButton
import dev.luisbaena.prodentclient.presentation.ui.components.common.dialogs.SuccessDialog
import dev.luisbaena.prodentclient.presentation.viewmodel.AuthViewModel

    /**
      * Pantalla para cambiar la contraseña del usuario
      * Permite ingresar una nueva contraseña y confirmarla
      * Muestra diálogos de éxito o error según el resultado
      */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    onOpenDrawer: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()

    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Cabecera(
                titulo = "Cambiar Contraseña",
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Información
            InfoCard(
                message = "Requisitos de la contraseña:\n• Mínimo 6 caracteres\n• Debe contener letras y números"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // NUEVA CONTRASEÑA
            PasswordTextField(
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    authViewModel.clearError()
                },
                label = "Nueva contraseña",
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            // CONFIRMAR CONTRASEÑA
            PasswordTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    authViewModel.clearError()
                },
                label = "Confirmar contraseña",
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
                isError = confirmPassword.isNotEmpty() && newPassword != confirmPassword,
                supportingText = {
                    if (confirmPassword.isNotEmpty() && newPassword != confirmPassword) {
                        Text(
                            text = "Las contraseñas no coinciden",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Error message
            uiState.errorMessage?.let { error ->
                ErrorCard(errorMessage = error)
            }

            // BOTÓN CAMBIAR CONTRASEÑA
            PrimaryLoadingButton(
                text = "Cambiar Contraseña",
                onClick = {
                    authViewModel.changePassword(newPassword, confirmPassword) {
                        showSuccessDialog = true
                    }
                },
                enabled = newPassword.isNotBlank() &&
                        confirmPassword.isNotBlank() &&
                        newPassword == confirmPassword &&
                        newPassword.length >= 6,
                isLoading = uiState.isLoading,
                loadingText = "Cambiando",
                icon = Icons.Default.Check,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
            )

            // BOTÓN CANCELAR
            SecondaryButton(
                text = "Cancelar",
                onClick = { navController.popBackStack() },
                enabled = !uiState.isLoading
            )
        }
    }

    // DIÁLOGO DE ÉXITO
    SuccessDialog(
        show = showSuccessDialog,
        title = "¡Contraseña actualizada!",
        message = "Tu contraseña ha sido cambiada correctamente.",
        onConfirm = {
            showSuccessDialog = false
            navController.popBackStack()
        }
    )
}