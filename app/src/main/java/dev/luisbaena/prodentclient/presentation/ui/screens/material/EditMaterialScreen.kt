package dev.luisbaena.prodentclient.presentation.ui.screens.material

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import dev.luisbaena.prodentclient.data.remote.dto.material.MaterialUpdateDTO
import dev.luisbaena.prodentclient.presentation.ui.components.BottomNavigationBar
import dev.luisbaena.prodentclient.presentation.ui.components.Cabecera
import dev.luisbaena.prodentclient.presentation.ui.components.common.buttons.PrimaryLoadingButton
import dev.luisbaena.prodentclient.presentation.ui.components.common.buttons.SecondaryButton
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.ErrorCard
import dev.luisbaena.prodentclient.presentation.ui.components.common.dialogs.SuccessDialog
import dev.luisbaena.prodentclient.presentation.ui.components.common.inputs.CustomTextField
import dev.luisbaena.prodentclient.presentation.ui.components.common.inputs.LabeledSwitch
import dev.luisbaena.prodentclient.presentation.viewmodel.MaterialViewModel

    /**
     * Pantalla para editar un material existente
     * Permite modificar el nombre, descripción y estado activo/inactivo del material
     */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMaterialScreen(
    modifier: Modifier = Modifier,
    materialId: String,
    viewModel: MaterialViewModel = hiltViewModel(),
    navController: NavHostController,
    onOpenDrawer: () -> Unit
) {
    val formState by viewModel.formState.collectAsState()
    val detailState by viewModel.detailState.collectAsState()
    val scrollState = rememberScrollState()

    // Estados del formulario
    var nombre by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var activo by rememberSaveable { mutableStateOf(false) }

    // Validaciones
    var nombreError by rememberSaveable { mutableStateOf(false) }

    // Diálogo de éxito
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }

    // Flag para saber si ya se cargaron los datos
    var dataLoaded by rememberSaveable { mutableStateOf(false) }

    // Cargar material al entrar
    LaunchedEffect(materialId) {
        viewModel.loadMaterialById(materialId)
    }

    // Prellenar el formulario cuando se cargue el material
    LaunchedEffect(detailState.material) {
        detailState.material?.let { material ->
            if (!dataLoaded) {
                nombre = material.nombre
                descripcion = material.descripcion ?: ""
                activo = material.activo
                dataLoaded = true
            }
        }
    }

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
                titulo = "Editar Material",
                showMenuIcon = true,
                onMenuClick = onOpenDrawer,
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // ESTADO: CARGANDO MATERIAL
                detailState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Cargando datos del material...")
                        }
                    }
                }

                // ESTADO: ERROR AL CARGAR
                detailState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(detailState.error!!)
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { viewModel.loadMaterialById(materialId) }
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }

                // ESTADO: FORMULARIO CARGADO
                detailState.material != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
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
                            leadingIcon = Icons.AutoMirrored.Filled.Label,
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

                        // Switch de activo/inactivo
                        LabeledSwitch(
                            label = "Material activo",
                            checked = activo,
                            onCheckedChange = { isChecked ->
                                activo = isChecked
                            },
                            modifier = Modifier.fillMaxWidth()
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

                        // BOTÓN ACTUALIZAR
                        PrimaryLoadingButton(
                            text = "Actualizar Material",
                            isLoading = formState.isLoading,
                            onClick = {
                                // Validaciones
                                nombreError = nombre.isBlank()

                                if (nombreError) {
                                    return@PrimaryLoadingButton
                                }

                                // Actualizar material
                                val material = MaterialUpdateDTO(
                                    nombre = nombre.trim(),
                                    descripcion = descripcion.trim().ifBlank { null },
                                    activo = activo
                                )

                                viewModel.updateMaterial(materialId, material) { }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // BOTÓN CANCELAR
                        SecondaryButton(
                            text = "Cancelar",
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        )
                    }
                }
            }
        }
    }

    // DIÁLOGO DE ÉXITO
    if (showSuccessDialog) {
        SuccessDialog(
            show = true,
            title = "¡Material actualizado!",
            message = "Los cambios se han guardado correctamente.",
            onConfirm = {
                showSuccessDialog = false
                viewModel.resetFormState()
                navController.popBackStack()
            }
        )
    }
}