package dev.luisbaena.prodentclient.presentation.ui.screens.material


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import dev.luisbaena.prodentclient.data.remote.dto.material.MaterialRequestDTO
import dev.luisbaena.prodentclient.presentation.ui.components.BottomNavigationBar
import dev.luisbaena.prodentclient.presentation.ui.components.Cabecera
import dev.luisbaena.prodentclient.presentation.ui.components.common.buttons.PrimaryLoadingButton
import dev.luisbaena.prodentclient.presentation.ui.components.common.buttons.SecondaryButton
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.ErrorCard
import dev.luisbaena.prodentclient.presentation.ui.components.common.dialogs.SuccessDialog
import dev.luisbaena.prodentclient.presentation.ui.components.common.inputs.CustomTextField
import dev.luisbaena.prodentclient.presentation.viewmodel.MaterialViewModel

/**
 * Pantalla para crear un nuevo material
 * Incluye un formulario con campos para nombre y descripción
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMaterialScreen(
    modifier: Modifier = Modifier,
    viewModel: MaterialViewModel = hiltViewModel(),
    navController: NavHostController,
    onOpenDrawer: () -> Unit
) {
    val formState by viewModel.formState.collectAsState()
    val scrollState = rememberScrollState()

    // Estados del formulario
    var nombre by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }

    // Validaciones
    var nombreError by rememberSaveable { mutableStateOf(false) }

    // Diálogo de éxito
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }

    // Observar estado de éxito
    LaunchedEffect(formState.isSuccess) {
        if (formState.isSuccess) {
            showSuccessDialog = true
        }
    }

    // Limpiar al salir
    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetFormState()
        }
    }

    Scaffold(
        topBar = {
            Cabecera(
                titulo = "Crear Material",
                showMenuIcon = true,
                onMenuClick = onOpenDrawer,
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // INFORMACIÓN DEL MATERIAL
            Text(
                text = "Información del material",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Nombre
            CustomTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    nombreError = false
                },
                label = "Nombre *",
                placeholder = "Ej: Zirconio",
                leadingIcon = Icons.Default.Label,
                isError = nombreError,
                errorMessage = "El nombre es obligatorio",
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Descripción
            CustomTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = "Descripción (opcional)",
                placeholder = "Ej: Material cerámico de alta resistencia para prótesis dentales",
                leadingIcon = Icons.Default.Description,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
            )

            Spacer(modifier = Modifier.height(24.dp))

            // MENSAJE DE ERROR
            if (formState.error != null) {
                ErrorCard(
                    errorMessage = formState.error!!,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // BOTÓN CREAR
            PrimaryLoadingButton(
                text = "Crear Material",
                isLoading = formState.isLoading,
                onClick = {
                    // Validaciones
                    nombreError = nombre.isBlank()

                    if (nombreError) {
                        return@PrimaryLoadingButton
                    }

                    // Crear material
                    val material = MaterialRequestDTO(
                        nombre = nombre.trim(),
                        descripcion = descripcion.trim().ifBlank { null }
                    )

                    viewModel.createMaterial(material) { }
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            //  BOTÓN CANCELAR
            SecondaryButton(
                text = "Cancelar",
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth(
                )
            )
        }
    }

    // DIÁLOGO DE ÉXITO
    if (showSuccessDialog) {
        SuccessDialog(
            show = showSuccessDialog,
            title = "¡Material creado!",
            message = "El material ha sido creado exitosamente.",
            onConfirm = {
                showSuccessDialog = false
                viewModel.resetFormState()
                navController.popBackStack()
            }
        )
    }
}