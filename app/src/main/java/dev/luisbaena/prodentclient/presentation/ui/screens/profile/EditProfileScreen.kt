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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.luisbaena.prodentclient.presentation.ui.components.Cabecera
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.ErrorCard
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.InfoCard
import dev.luisbaena.prodentclient.presentation.ui.components.common.buttons.PrimaryLoadingButton
import dev.luisbaena.prodentclient.presentation.ui.components.common.buttons.SecondaryButton
import dev.luisbaena.prodentclient.presentation.ui.components.common.dialogs.SuccessDialog
import dev.luisbaena.prodentclient.presentation.ui.components.common.inputs.CustomTextField
import dev.luisbaena.prodentclient.presentation.viewmodel.AuthViewModel

   /**
     * Pantalla para editar el perfil del usuario
     * Permite actualizar nombre, apellido, email y teléfono
     * Muestra diálogos de éxito o error según el resultado de la actualización
     */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    onOpenDrawer: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()
    val user = uiState.user

    var nombre by rememberSaveable { mutableStateOf(user?.nombre ?: "") }
    var apellido by rememberSaveable { mutableStateOf(user?.apellido ?: "") }
    var telefono by rememberSaveable { mutableStateOf(user?.telefono ?: "") }
    var email by rememberSaveable { mutableStateOf(user?.email ?: "") }

    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }
    var isUpdating by rememberSaveable { mutableStateOf(false) }

    // Detectar cuando la actualización termina exitosamente
    LaunchedEffect(uiState.isLoading, uiState.errorMessage) {
        if (isUpdating && !uiState.isLoading && uiState.errorMessage == null) {
            showSuccessDialog = true
            isUpdating = false
        } else if (isUpdating && !uiState.isLoading && uiState.errorMessage != null) {
            isUpdating = false
        }
    }

    Scaffold(
        topBar = {
            Cabecera(
                titulo = "Editar Perfil",
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
                message = "Actualiza tu información personal"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // NOMBRE
            CustomTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    authViewModel.clearError()
                },
                label = "Nombre",
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Person,
            )

            // APELLIDO
            CustomTextField(
                value = apellido,
                onValueChange = {
                    apellido = it
                    authViewModel.clearError()
                },
                label = "Apellido",
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Person,
            )

            // EMAIL
            CustomTextField(
                value = email,
                onValueChange = {
                    email = it
                    authViewModel.clearError()
                },
                label = "Correo electrónico",
                keyboardType = KeyboardType.Email,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Email
            )

            // TELÉFONO
            CustomTextField(
                value = telefono,
                onValueChange = {
                    telefono = it
                    authViewModel.clearError()
                },
                label = "Teléfono",
                keyboardType = KeyboardType.Phone,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Phone,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Error message
            uiState.errorMessage?.let { error ->
                ErrorCard(errorMessage = error)
            }

            // BOTÓN GUARDAR
            PrimaryLoadingButton(
                text = "Guardar Cambios",
                onClick = {
                    isUpdating = true
                    authViewModel.updateProfile(nombre, apellido, email, telefono)
                },
                enabled = nombre.isNotBlank() &&
                        apellido.isNotBlank() &&
                        telefono.isNotBlank() &&
                        email.isNotBlank(),
                isLoading = uiState.isLoading,
                loadingText = "Guardando",
                icon = Icons.Default.Save,
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
        show = showSuccessDialog && !uiState.isLoading && uiState.errorMessage == null,
        title = "¡Perfil actualizado!",
        message = "Tu información ha sido actualizada correctamente.",
        onConfirm = {
            showSuccessDialog = false
            navController.popBackStack()
        }
    )
}