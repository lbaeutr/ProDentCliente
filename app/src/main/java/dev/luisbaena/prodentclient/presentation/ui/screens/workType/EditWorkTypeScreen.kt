package dev.luisbaena.prodentclient.presentation.ui.screens.workType


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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import dev.luisbaena.prodentclient.data.remote.dto.workType.WorkTypeUpdateDTO
import dev.luisbaena.prodentclient.domain.model.WorkTypeCategory
import dev.luisbaena.prodentclient.presentation.ui.components.BottomNavigationBar
import dev.luisbaena.prodentclient.presentation.ui.components.Cabecera
import dev.luisbaena.prodentclient.presentation.ui.components.common.buttons.PrimaryLoadingButton
import dev.luisbaena.prodentclient.presentation.ui.components.common.buttons.SecondaryButton
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.ErrorCard
import dev.luisbaena.prodentclient.presentation.ui.components.common.dialogs.SuccessDialog
import dev.luisbaena.prodentclient.presentation.ui.components.common.inputs.CustomDropdown
import dev.luisbaena.prodentclient.presentation.ui.components.common.inputs.CustomTextField
import dev.luisbaena.prodentclient.presentation.ui.components.common.inputs.LabeledSwitch
import dev.luisbaena.prodentclient.presentation.viewmodel.WorkTypeViewModel

/**
 * Pantalla para editar un tipo de trabajo existente
 *  contiene un formulario prellenado con los datos actuales del tipo de trabajo
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditWorkTypeScreen(
    modifier: Modifier = Modifier,
    workTypeId: String,
    viewModel: WorkTypeViewModel = hiltViewModel(),
    navController: NavHostController,
    onOpenDrawer: () -> Unit
) {
    val formState by viewModel.formState.collectAsState()
    val detailState by viewModel.detailState.collectAsState()
    val scrollState = rememberScrollState()

    // Estados del formulario
    var selectedCategory by rememberSaveable { mutableStateOf("") }
    var selectedCategoryDisplay by rememberSaveable { mutableStateOf("") }
    var nombre by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var precioBase by rememberSaveable { mutableStateOf("") }
    var activo by rememberSaveable { mutableStateOf(true) }

    // Validaciones
    var categoryError by rememberSaveable { mutableStateOf(false) }
    var nombreError by rememberSaveable { mutableStateOf(false) }
    var precioError by rememberSaveable { mutableStateOf(false) }

    // Diálogo de éxito
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }

    // Flag para saber si ya se cargaron los datos
    var dataLoaded by rememberSaveable { mutableStateOf(false) }

    // Cargar tipo de trabajo al entrar
    LaunchedEffect(workTypeId) {
        viewModel.loadWorkTypeById(workTypeId)
    }

    // Prellenar el formulario cuando se cargue el tipo de trabajo
    LaunchedEffect(detailState.workType) {
        detailState.workType?.let { workType ->
            if (!dataLoaded) {
                selectedCategory = workType.categoria
                selectedCategoryDisplay =
                    WorkTypeCategory.fromValue(workType.categoria)?.displayName
                        ?: workType.categoria
                nombre = workType.nombre
                descripcion = workType.descripcion ?: ""
                precioBase = workType.precioBase?.toString() ?: ""
                activo = workType.activo
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
                titulo = "Editar Tipo de Trabajo",
                showMenuIcon = true,
                onMenuClick = onOpenDrawer,
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // ESTADO: CARGANDO TIPO DE TRABAJO
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
                            Text("Cargando datos del tipo de trabajo...")
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
                                onClick = { viewModel.loadWorkTypeById(workTypeId) }
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }

                // ESTADO: FORMULARIO CARGADO
                detailState.workType != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp)
                    ) {
                        // SELECCIONAR CATEGORÍA
                        Text(
                            text = "Categoría",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Dropdown de categorías
                        CustomDropdown(
                            value = selectedCategoryDisplay,
                            label = "Seleccionar categoría *",
                            placeholder = "Ej: Prótesis Fija",
                            items = WorkTypeCategory.entries,
                            onItemSelected = { category ->
                                selectedCategory = category.value
                                selectedCategoryDisplay = category.displayName
                                categoryError = false
                            },
                            itemToString = { it.displayName },
                            leadingIcon = Icons.Default.Category,
                            isError = categoryError,
                            errorMessage = "Debes seleccionar una categoría",
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // DATOS DEL TIPO DE TRABAJO
                        Text(
                            text = "Información del trabajo",
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
                            placeholder = "Ej: Corona Metal-Cerámica",
                            leadingIcon = Icons.AutoMirrored.Filled.Label,
                            isError = nombreError,
                            errorMessage = "El nombre es obligatorio",
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Descripción
                        CustomTextField(
                            value = descripcion,
                            onValueChange = { descripcion = it },
                            label = "Descripción (opcional)",
                            placeholder = "Ej: Corona completa con estructura metálica y cerámica estética",
                            leadingIcon = Icons.Default.Description,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Precio Base
                        CustomTextField(
                            value = precioBase,
                            onValueChange = {
                                // Solo permitir números y punto decimal
                                // Regex para permitir solo números y un punto decimal funciona de la siguiente manera:
                                // ^ inicio de la cadena, \\d* cero o más dígitos, \\.? un punto opcional, \\d* cero o más dígitos, $ fin de la cadena
                                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                                    precioBase = it
                                    precioError = false
                                }
                            },
                            label = "Precio Base (opcional)",
                            placeholder = "Ej: 150.00",
                            leadingIcon = Icons.Default.Euro,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardType = KeyboardType.Decimal,
                            isError = precioError,
                            errorMessage = "Precio inválido",
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Activar o desactivar el tipo de trabajo
                        LabeledSwitch(
                            label = "Tipo de trabajo activo",
                            checked = activo,
                            onCheckedChange = { isActive ->
                                activo = isActive
                            }
                        )

                        Spacer(modifier = Modifier.height(24.dp))


                        // MENSAJE DE ERROR
                        if (formState.error != null) {
                            ErrorCard(
                                errorMessage = formState.error!!,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Error,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = formState.error!!,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // BOTÓN ACTUALIZAR
                        PrimaryLoadingButton(
                            text = "Actualizar Tipo de Trabajo",
                            isLoading = formState.isLoading,
                            onClick = {
                                // Validaciones
                                categoryError = selectedCategory.isBlank()
                                nombreError = nombre.isBlank()

                                // Validar precio si se ingresó
                                if (precioBase.isNotBlank()) {
                                    precioError = precioBase.toDoubleOrNull()?.let { it < 0 } ?: true
                                }

                                if (categoryError || nombreError || precioError) {
                                    return@PrimaryLoadingButton
                                }

                                // Actualizar tipo de trabajo
                                val workType = WorkTypeUpdateDTO(
                                    categoria = selectedCategory,
                                    nombre = nombre.trim(),
                                    descripcion = descripcion.trim().ifBlank { null },
                                    precioBase = precioBase.trim().ifBlank { null }
                                        ?.toDoubleOrNull(),
                                    activo = activo
                                )

                                viewModel.updateWorkType(workTypeId, workType) { }
                            }
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // BOTÓN CANCELAR
                        SecondaryButton(
                            text = "Cancelar",
                            onClick = {
                                viewModel.resetFormState()
                                navController.popBackStack()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(
                                    56.dp
                                )
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
            title = "¡Tipo de trabajo actualizado!",
            message = "Los cambios se han guardado correctamente.\n\nCategoría: $selectedCategoryDisplay\nNombre: $nombre",
            onConfirm = {
                showSuccessDialog = false
                viewModel.resetFormState()
                navController.popBackStack()
            },
            confirmButtonText = "Aceptar"
        )
    }
}