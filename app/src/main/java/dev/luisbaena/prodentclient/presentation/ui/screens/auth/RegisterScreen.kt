package dev.luisbaena.prodentclient.presentation.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import dev.luisbaena.prodentclient.presentation.ui.components.BottomNavigationBar
import dev.luisbaena.prodentclient.presentation.ui.components.Cabecera
import dev.luisbaena.prodentclient.presentation.ui.components.common.buttons.PrimaryLoadingButton
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.ErrorCard
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.InfoCard
import dev.luisbaena.prodentclient.presentation.ui.components.common.dialogs.SuccessDialog
import dev.luisbaena.prodentclient.presentation.ui.components.common.inputs.CustomTextField
import dev.luisbaena.prodentclient.presentation.ui.components.common.inputs.PasswordTextField
import dev.luisbaena.prodentclient.presentation.viewmodel.AuthViewModel

/**
 * Pantalla de registro de usuario
 * Permite al administrador crear una nueva cuenta de usuario
 * Incluye campos para nombre, apellidos, email, teléfono y contraseña
 * Muestra diálogos de éxito o error según el resultado del registro
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
    onOpenDrawer: () -> Unit,
    navController: NavHostController
) {
    //  RESETEAR isSuccess al entrar a la pantalla (solo una vez)
    LaunchedEffect(Unit) {
        viewModel.clearError()
    }

    Scaffold(
        topBar = {
            Cabecera(
                titulo = "Registro de Usuario",
                showMenuIcon = true,
                onMenuClick = onOpenDrawer
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->

        val scrollState = rememberScrollState()
        val uiState by viewModel.uiState.collectAsState()
        val focusManager = LocalFocusManager.current

        var name by rememberSaveable { mutableStateOf("") }
        var lastname by rememberSaveable { mutableStateOf("") }
        var email by rememberSaveable { mutableStateOf("") }
        var phone by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("Segura123!") }
        var passwordRepeat by rememberSaveable { mutableStateOf("Segura123!") }

        var showSuccessDialog by rememberSaveable { mutableStateOf(false) }

        // Observar éxito del registro
        LaunchedEffect(uiState.isSuccess) {
            if (uiState.isSuccess) {
                showSuccessDialog = true
            }
        }

        // Usar tu componente SuccessDialog
        SuccessDialog(
            show = showSuccessDialog,
            title = "Usuario registrado",
            message = "El usuario ha sido registrado exitosamente",
            onConfirm = {
                showSuccessDialog = false
                viewModel.clearError()  //Esto resetea isSuccess

                // Limpiar campos después de cerrar el diálogo
                name = ""
                lastname = ""
                email = ""
                phone = ""
                password = "Segura123!"
                passwordRepeat = "Segura123!"

                onRegisterSuccess()
            }
        )

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TODO: Implementar infoCard!!
            InfoCard(
                message = "Por favor, completa el formulario para crear una nueva cuenta."
            )
            Spacer(modifier = Modifier.height(16.dp))



            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Registristro de nuevo usuario",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            CustomTextField(
                value = name,
                onValueChange = { name = it },
                label = "Nombre",
                placeholder = "Tu nombre",
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Person,
            )

            Spacer(modifier = Modifier.height(12.dp))

            CustomTextField(
                value = lastname,
                onValueChange = { lastname = it },
                label = "Apellidos",
                placeholder = "Tus apellidos",
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(12.dp))

            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = "Correo electrónico",
                placeholder = "ejemplo@mail.com",
                keyboardType = KeyboardType.Email,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Email,
            )

            Spacer(modifier = Modifier.height(12.dp))

            CustomTextField(
                value = phone,
                onValueChange = { phone = it },
                label = "Teléfono",
                placeholder = "Tu número de teléfono",
                keyboardType = KeyboardType.Phone,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Phone,
            )

            Spacer(modifier = Modifier.height(12.dp))

            PasswordTextField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                placeholder = "Mínimo 6 caracteres",
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(12.dp))

            PasswordTextField(
                value = passwordRepeat,
                onValueChange = { passwordRepeat = it },
                label = "Repite la contraseña",
                placeholder = "Vuelve a escribir la contraseña",
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(24.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryLoadingButton(
                text = "Crear cuenta",
                onClick = {
                    focusManager.clearFocus()
                    viewModel.register(
                        name = name,
                        lastname = lastname,
                        email = email,
                        phone = phone,
                        password = password,
                        passwordRepeat = passwordRepeat
                    )
                },
                enabled = name.isNotBlank() &&
                        lastname.isNotBlank() &&
                        email.isNotBlank() &&
                        phone.isNotBlank() &&
                        password.isNotBlank() &&
                        passwordRepeat.isNotBlank(),
                isLoading = uiState.isLoading,
                loadingText = "Registrando...",
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}