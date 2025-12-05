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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import dev.luisbaena.prodentclient.data.remote.dto.workType.WorkTypeRequestDTO
import dev.luisbaena.prodentclient.domain.model.WorkTypeCategory
import dev.luisbaena.prodentclient.presentation.ui.components.BottomNavigationBar
import dev.luisbaena.prodentclient.presentation.ui.components.Cabecera
import dev.luisbaena.prodentclient.presentation.ui.components.common.buttons.PrimaryLoadingButton
import dev.luisbaena.prodentclient.presentation.ui.components.common.buttons.SecondaryButton
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.ErrorCard
import dev.luisbaena.prodentclient.presentation.ui.components.common.dialogs.SuccessDialog
import dev.luisbaena.prodentclient.presentation.ui.components.common.inputs.CustomDropdown
import dev.luisbaena.prodentclient.presentation.ui.components.common.inputs.CustomTextField
import dev.luisbaena.prodentclient.presentation.viewmodel.WorkTypeViewModel

    /**
     * Pantalla para crear un nuevo tipo de trabajo
     *
     */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkTypeScreen(
    modifier: Modifier = Modifier,
    viewModel: WorkTypeViewModel = hiltViewModel(),
    navController: NavHostController,
    onOpenDrawer: () -> Unit
) {
    val formState by viewModel.formState.collectAsState()
    val scrollState = rememberScrollState()

    // Data class para adaptar WorkTypeCategory al CustomDropdown
    data class CategoryItem(val displayName: String, val value: String)

    // Estados del formulario
    var selectedCategory by rememberSaveable { mutableStateOf("") }
    var selectedCategoryDisplay by rememberSaveable { mutableStateOf("") }
    var nombre by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var precioBase by rememberSaveable { mutableStateOf("") }

    // Items para el dropdown
    val categoryItems = remember {
        WorkTypeCategory.entries.map { CategoryItem(it.displayName, it.value) }
    }

    // Validaciones
    var categoryError by rememberSaveable { mutableStateOf(false) }
    var nombreError by rememberSaveable { mutableStateOf(false) }
    var precioError by rememberSaveable { mutableStateOf(false) }

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
                titulo = "Crear Tipo de Trabajo",
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
                items = categoryItems,
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

            Spacer(modifier = Modifier.height(24.dp))

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
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
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
//                maxLines = 4,
//                minLines = 3
                // Multilínea TODO
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Precio Base
            CustomTextField(
                value = precioBase,
                onValueChange = {
                    // Solo permitir números y punto decimal
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
                text = "Crear Tipo de Trabajo",
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

                    // Crear tipo de trabajo
                    val workType = WorkTypeRequestDTO(
                        categoria = selectedCategory,
                        nombre = nombre.trim(),
                        descripcion = descripcion.trim().ifBlank { null },
                        precioBase = precioBase.trim().ifBlank { null }?.toDoubleOrNull()
                    )

                    viewModel.createWorkType(workType) { }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !formState.isLoading
            )

            Spacer(modifier = Modifier.height(18.dp))

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

    // DIÁLOGO DE ÉXITO
    if (showSuccessDialog) {
        SuccessDialog(
            show = showSuccessDialog,
            title = "¡Tipo de trabajo creado!",
            message = "El tipo de trabajo ha sido creado exitosamente.",
            onConfirm = {
                showSuccessDialog = false
                viewModel.resetFormState()
                navController.popBackStack()
            }
        )
    }
}