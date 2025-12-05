package dev.luisbaena.prodentclient.presentation.ui.screens.work

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import dev.luisbaena.prodentclient.data.remote.dto.work.WorkUpdateDTO
import dev.luisbaena.prodentclient.presentation.ui.components.BottomNavigationBar
import dev.luisbaena.prodentclient.presentation.ui.components.Cabecera
import dev.luisbaena.prodentclient.presentation.ui.components.common.buttons.PrimaryLoadingButton
import dev.luisbaena.prodentclient.presentation.ui.components.common.buttons.SecondaryButton
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.ErrorCard
import dev.luisbaena.prodentclient.presentation.ui.components.common.dialogs.SuccessDialog
import dev.luisbaena.prodentclient.presentation.ui.components.common.inputs.CustomDropdown
import dev.luisbaena.prodentclient.presentation.ui.components.common.inputs.CustomTextField
import dev.luisbaena.prodentclient.presentation.viewmodel.*


    /**
     * Pantalla de edición de trabajo
     * Permite editar los detalles de un trabajo existente
     */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkEditScreen(
    workId: String,
    navController: NavHostController,
    viewModel: WorkViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    clinicViewModel: ClinicViewModel = hiltViewModel(),
    dentistViewModel: DentistViewModel = hiltViewModel(),
    workTypeViewModel: WorkTypeViewModel = hiltViewModel(),
    materialViewModel: MaterialViewModel = hiltViewModel(),
    onOpenDrawer: () -> Unit
) {
    val detailState by viewModel.detailState.collectAsState()
    val formState by viewModel.formState.collectAsState()
    val authUiState by authViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    // FocusRequesters para cada campo
    val numeroTrabajoFocusRequester = remember { FocusRequester() }
    val pacienteFocusRequester = remember { FocusRequester() }
    val precioFocusRequester = remember { FocusRequester() }

    // Variables para almacenar posiciones Y de cada campo
    var numeroTrabajoYPosition by remember { mutableStateOf(0f) }
    var clinicaYPosition by remember { mutableStateOf(0f) }
    var dentistaYPosition by remember { mutableStateOf(0f) }
    var tipoTrabajoYPosition by remember { mutableStateOf(0f) }
    var pacienteYPosition by remember { mutableStateOf(0f) }
    var precioYPosition by remember { mutableStateOf(0f) }

    // Verificar si el usuario es administrador
    val isAdmin = authUiState.user?.role == "ADMIN"

    // Listas para dropdowns
    val clinicsState by clinicViewModel.uiState.collectAsState()
    val dentistsState by dentistViewModel.listState.collectAsState()
    val workTypesState by workTypeViewModel.listState.collectAsState()
    val materialsState by materialViewModel.listState.collectAsState()

    // Estados del formulario - BÁSICOS
    var numeroTrabajo by rememberSaveable { mutableStateOf("") }
    var selectedClinicaId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedClinicaName by rememberSaveable { mutableStateOf("") }
    var selectedDentistaId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedDentistaName by rememberSaveable { mutableStateOf("") }
    var selectedTipoTrabajoId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedTipoTrabajoName by rememberSaveable { mutableStateOf("") }
    var selectedMaterialId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedMaterialName by rememberSaveable { mutableStateOf("") }
    var pacienteNombre by rememberSaveable { mutableStateOf("") }
    var piezasDentales by rememberSaveable { mutableStateOf("") }
    var precio by rememberSaveable { mutableStateOf("") }

    // Estados del formulario - OPCIONALES
    var descripcionTrabajo by rememberSaveable { mutableStateOf("") }
    var color by rememberSaveable { mutableStateOf("") }
    var guiaColor by rememberSaveable { mutableStateOf("") }
    var observaciones by rememberSaveable { mutableStateOf("") }
    var ajustePrecio by rememberSaveable { mutableStateOf("") }
    var urgente by rememberSaveable { mutableStateOf(false) }
    var fechaEntregaEstimada by rememberSaveable { mutableStateOf("") }

    // Estados de validación
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    // Errores de validación
    var numeroTrabajoError by rememberSaveable { mutableStateOf(false) }
    var clinicaError by rememberSaveable { mutableStateOf(false) }
    var dentistaError by rememberSaveable { mutableStateOf(false) }
    var tipoTrabajoError by rememberSaveable { mutableStateOf(false) }
    var pacienteError by rememberSaveable { mutableStateOf(false) }
    var precioError by rememberSaveable { mutableStateOf(false) }

    // Date picker state
    val datePickerState = rememberDatePickerState()

    // Cargar datos iniciales
    LaunchedEffect(Unit) {
        clinicViewModel.loadClinicas()
        workTypeViewModel.loadWorkTypes()
        materialViewModel.loadMaterials()
    }

    // Cargar datos del trabajo
    LaunchedEffect(workId) {
        viewModel.loadWorkById(workId)
    }

    // Cargar dentistas cuando se selecciona clínica
    LaunchedEffect(selectedClinicaId) {
        selectedClinicaId?.let {
            dentistViewModel.loadDentists(forceRefresh = true)
        }
    }

    // Inicializar campos cuando se cargue el trabajo
    LaunchedEffect(detailState.work) {
        detailState.work?.let { work ->
            numeroTrabajo = work.numeroTrabajo
            selectedClinicaId = work.clinicaId
            selectedClinicaName = work.clinicaNombre
            selectedDentistaId = work.dentistaId
            selectedDentistaName = "${work.dentistaNombre} ${work.dentistaApellidos}"
            selectedTipoTrabajoId = work.tipoTrabajoId
            selectedTipoTrabajoName = work.tipoTrabajoNombre
            selectedMaterialId = work.materialId
            selectedMaterialName = work.materialNombre ?: ""
            pacienteNombre = work.pacienteNombre
            descripcionTrabajo = work.descripcionTrabajo ?: ""
            piezasDentales = work.piezasDentales
            color = work.color ?: ""
            guiaColor = work.guiaColor ?: ""
            observaciones = work.observaciones ?: ""
            precio = work.precio.toString()
            ajustePrecio = work.ajustePrecio ?: ""
            urgente = work.urgente
            fechaEntregaEstimada = work.fechaEntregaEstimada ?: ""
        }
    }

    // Manejar éxito
    LaunchedEffect(formState.isSuccess) {
        if (formState.isSuccess) {
            showSuccessDialog = true
        }
    }

    Scaffold(
        topBar = {
            Cabecera(
                titulo = "Editar Trabajo",
                showMenuIcon = true,
                onMenuClick = onOpenDrawer,
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // LOADING
                detailState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                // ERROR
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
                            Text(
                                text = detailState.error!!,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // FORMULARIO
                detailState.work != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp)
                    ) {
                        // ============================================
                        // SECCIÓN 0: NÚMERO DE TRABAJO
                        // ============================================
                        Text(
                            text = "Número de Trabajo",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CustomTextField(
                            value = numeroTrabajo,
                            onValueChange = {
                                numeroTrabajo = it
                                numeroTrabajoError = false
                            },
                            label = "Número de Trabajo *",
                            leadingIcon = Icons.Default.Work,
                            keyboardType = KeyboardType.Number,
                            isError = numeroTrabajoError,
                            errorMessage = "El número de trabajo es obligatorio",
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(numeroTrabajoFocusRequester)
                                .onGloballyPositioned { coordinates ->
                                    numeroTrabajoYPosition = coordinates.positionInParent().y
                                }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // ============================================
                        // SECCIÓN 1: CLÍNICA Y DENTISTA
                        // ============================================
                        Text(
                            text = "Clínica y Dentista",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Clínica
                        CustomDropdown(
                            value = selectedClinicaName,
                            label = "Clínica *",
                            placeholder = "Seleccionar clínica",
                            items = clinicsState.clinicas,
                            onItemSelected = { clinica ->
                                selectedClinicaId = clinica.id
                                selectedClinicaName = clinica.nombre
                                clinicaError = false
                                // Resetear dentista al cambiar clínica
                                selectedDentistaId = null
                                selectedDentistaName = ""
                            },
                            itemToString = { it.nombre },
                            leadingIcon = Icons.Default.Business,
                            isError = clinicaError,
                            errorMessage = "Debes seleccionar una clínica",
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coordinates ->
                                    clinicaYPosition = coordinates.positionInParent().y
                                }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Dentista
                        CustomDropdown(
                            value = selectedDentistaName,
                            label = "Dentista *",
                            placeholder = "Seleccionar dentista",
                            items = dentistsState.dentists.filter {
                                it.clinicaNombre == selectedClinicaName && it.activo
                            },
                            onItemSelected = { dentista ->
                                selectedDentistaId = dentista.id
                                selectedDentistaName = "${dentista.nombre} ${dentista.apellidos}"
                                dentistaError = false
                            },
                            itemToString = { "${it.nombre} ${it.apellidos}" },
                            leadingIcon = Icons.Default.MedicalServices,
                            isError = dentistaError,
                            errorMessage = "Debes seleccionar un dentista",
                            enabled = selectedClinicaId != null,
                            emptyMessage = "Primero selecciona una clínica",
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coordinates ->
                                    dentistaYPosition = coordinates.positionInParent().y
                                }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // ============================================
                        // SECCIÓN 2: TIPO DE TRABAJO Y MATERIAL
                        // ============================================
                        Text(
                            text = "Tipo de Trabajo y Material",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Tipo de Trabajo
                        CustomDropdown(
                            value = selectedTipoTrabajoName,
                            label = "Tipo de Trabajo *",
                            placeholder = "Seleccionar tipo",
                            items = workTypesState.workTypes.filter { it.activo },
                            onItemSelected = { workType ->
                                selectedTipoTrabajoId = workType.id
                                selectedTipoTrabajoName = workType.nombre
                                tipoTrabajoError = false
                            },
                            itemToString = { it.nombre },
                            leadingIcon = Icons.Default.Category,
                            isError = tipoTrabajoError,
                            errorMessage = "Debes seleccionar un tipo de trabajo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coordinates ->
                                    tipoTrabajoYPosition = coordinates.positionInParent().y
                                }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Material
                        CustomDropdown(
                            value = selectedMaterialName.ifBlank { "Sin material" },
                            label = "Material (opcional)",
                            placeholder = "Seleccionar material",
                            items = materialsState.materials.filter { it.activo },
                            onItemSelected = { material ->
                                selectedMaterialId = material.id
                                selectedMaterialName = material.nombre
                            },
                            itemToString = { it.nombre },
                            leadingIcon = Icons.Default.Science,
                            allowClear = true,
                            onClear = {
                                selectedMaterialId = null
                                selectedMaterialName = ""
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // ============================================
                        // SECCIÓN 3: INFORMACIÓN DEL PACIENTE
                        // ============================================
                        Text(
                            text = "Información del Paciente",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CustomTextField(
                            value = pacienteNombre,
                            onValueChange = {
                                pacienteNombre = it
                                pacienteError = false
                            },
                            label = "Nombre del Paciente *",
                            leadingIcon = Icons.Default.Person,
                            isError = pacienteError,
                            errorMessage = "El nombre del paciente es obligatorio",
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(pacienteFocusRequester)
                                .onGloballyPositioned { coordinates ->
                                    pacienteYPosition = coordinates.positionInParent().y
                                }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // ============================================
                        // SECCIÓN 4: DETALLES DEL TRABAJO
                        // ============================================
                        Text(
                            text = "Detalles del Trabajo",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Descripción
                        CustomTextField(
                            value = descripcionTrabajo,
                            onValueChange = { descripcionTrabajo = it },
                            label = "Descripción",
                            leadingIcon = Icons.Default.Description,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Piezas Dentales
                        CustomTextField(
                            value = piezasDentales,
                            onValueChange = { piezasDentales = it },
                            label = "Piezas Dentales *",
                            placeholder = "Ej: 11, 12, 13",
                            leadingIcon = Icons.Default.Medication,
                            keyboardType = KeyboardType.Text,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Color
                        CustomTextField(
                            value = color,
                            onValueChange = { color = it },
                            label = "Color",
                            leadingIcon = Icons.Default.Palette,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Guía de color
                        CustomTextField(
                            value = guiaColor,
                            onValueChange = { guiaColor = it },
                            label = "Guía de Color",
                            leadingIcon = Icons.Default.ColorLens,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // ============================================
                        // SECCIÓN 5: PRECIO Y AJUSTES (Solo Admin)
                        // ============================================
                        if (isAdmin) {
                            Text(
                                text = "Precio y Ajustes",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Precio
                            CustomTextField(
                                value = precio,
                                onValueChange = {
                                    // Solo permitir números y punto decimal
                                    if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                                        precio = it
                                        precioError = false
                                    }
                                },
                                label = "Precio (€) *",
                                leadingIcon = Icons.Default.Euro,
                                keyboardType = KeyboardType.Decimal,
                                isError = precioError,
                                errorMessage = "El precio es obligatorio y debe ser válido",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(precioFocusRequester)
                                    .onGloballyPositioned { coordinates ->
                                        precioYPosition = coordinates.positionInParent().y
                                    }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Ajuste de Precio
                            CustomTextField(
                                value = ajustePrecio,
                                onValueChange = { ajustePrecio = it },
                                label = "Ajuste de Precio",
                                placeholder = "Ej: Descuento 10%",
                                leadingIcon = Icons.Default.Discount,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        // ============================================
                        // SECCIÓN 6: FECHAS Y PRIORIDAD
                        // ============================================
                        Text(
                            text = "Fechas y Prioridad",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Fecha Entrega Estimada
                        CustomTextField(
                            value = fechaEntregaEstimada,
                            onValueChange = {},
                            label = "Fecha Entrega Estimada (opcional)",
                            placeholder = "Seleccionar fecha",
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(
                                        Icons.Default.CalendarToday,
                                        contentDescription = "Seleccionar fecha"
                                    )
                                }
                            },
                            leadingIcon = Icons.Default.Event,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Urgente Switch
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (urgente)
                                    MaterialTheme.colorScheme.errorContainer
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PriorityHigh,
                                        contentDescription = null,
                                        tint = if (urgente)
                                            MaterialTheme.colorScheme.error
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Trabajo Urgente",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = if (urgente) "Prioridad alta" else "Prioridad normal",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Switch(
                                    checked = urgente,
                                    onCheckedChange = { urgente = it }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // ============================================
                        // SECCIÓN 7: OBSERVACIONES
                        // ============================================
                        Text(
                            text = "Observaciones",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CustomTextField(
                            value = observaciones,
                            onValueChange = { observaciones = it },
                            label = "Observaciones",
                            placeholder = "Notas adicionales sobre el trabajo",
                            leadingIcon = Icons.AutoMirrored.Filled.Notes,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // ============================================
                        // BOTONES DE ACCIÓN
                        // ============================================
                        PrimaryLoadingButton(
                            text = "Guardar Cambios",
                            icon = Icons.Default.Save,
                            isLoading = formState.isLoading,
                            onClick = {
                                // Validaciones
                                numeroTrabajoError = numeroTrabajo.isBlank()
                                clinicaError = selectedClinicaId == null
                                dentistaError = selectedDentistaId == null
                                tipoTrabajoError = selectedTipoTrabajoId == null
                                pacienteError = pacienteNombre.isBlank()

                                // Solo validar precio si el usuario es administrador
                                precioError = if (isAdmin) {
                                    try {
                                        precio.isBlank() || precio.toDouble() <= 0
                                    } catch (_: Exception) {
                                        true
                                    }
                                } else {
                                    false // No validar para usuarios normales
                                }

                                if (numeroTrabajoError || clinicaError || dentistaError ||
                                    tipoTrabajoError || pacienteError || precioError
                                ) {
                                    // Hacer focus y scroll al primer campo con error
                                    coroutineScope.launch {
                                        val targetPosition = when {
                                            numeroTrabajoError -> {
                                                numeroTrabajoFocusRequester.requestFocus()
                                                numeroTrabajoYPosition
                                            }
                                            clinicaError -> clinicaYPosition
                                            dentistaError -> dentistaYPosition
                                            tipoTrabajoError -> tipoTrabajoYPosition
                                            pacienteError -> {
                                                pacienteFocusRequester.requestFocus()
                                                pacienteYPosition
                                            }
                                            precioError -> {
                                                precioFocusRequester.requestFocus()
                                                precioYPosition
                                            }
                                            else -> 0f
                                        }

                                        // Scroll con un pequeño offset hacia arriba
                                        val offset = with(density) { 100.dp.toPx() }
                                        scrollState.animateScrollTo((targetPosition - offset).toInt().coerceAtLeast(0))
                                    }
                                    return@PrimaryLoadingButton
                                }

                                // Crear DTO de actualización
                                val updateDto = WorkUpdateDTO(
                                    numeroTrabajo = numeroTrabajo.ifBlank { null },
                                    clinicaId = selectedClinicaId,
                                    dentistaId = selectedDentistaId,
                                    tipoTrabajoId = selectedTipoTrabajoId,
                                    materialId = selectedMaterialId?.ifBlank { null },
                                    pacienteNombre = pacienteNombre.ifBlank { null },
                                    descripcionTrabajo = descripcionTrabajo.ifBlank { null },
                                    piezasDentales = piezasDentales.ifBlank { null },
                                    color = color.ifBlank { null },
                                    guiaColor = guiaColor.ifBlank { null },
                                    observaciones = observaciones.ifBlank { null },
                                    precio = precio.toDoubleOrNull(), // Enviar precio siempre (puede ser auto-calculado)
                                    ajustePrecio = if (isAdmin) ajustePrecio.ifBlank { null } else null,
                                    urgente = urgente,
                                    fechaEntregaEstimada = fechaEntregaEstimada.ifBlank { null }
                                )

                                viewModel.updateWork(
                                    id = workId,
                                    work = updateDto,
                                    onSuccess = {
                                        showSuccessDialog = true
                                    }
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        SecondaryButton(
                            text = "Cancelar",
                            onClick = { navController.navigateUp() },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Mostrar error si hay
                        if (formState.error != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            ErrorCard(
                                errorMessage = formState.error!!,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Espaciado final
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

        // DIÁLOGO DE ÉXITO
        if (showSuccessDialog) {
            SuccessDialog(
                show = showSuccessDialog,
                title = "Trabajo Actualizado",
                message = "Los cambios se han guardado correctamente",
                onConfirm = {
                    showSuccessDialog = false
                    viewModel.resetFormState()
                    navController.navigateUp()
                }
            )
        }

        // DATE PICKER DIALOG
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                // Convertir millis a fecha en formato String (yyyy-MM-dd)
                                val calendar = java.util.Calendar.getInstance()
                                calendar.timeInMillis = millis
                                fechaEntregaEstimada = String.format(
                                    java.util.Locale.getDefault(),
                                    "%04d-%02d-%02d",
                                    calendar.get(java.util.Calendar.YEAR),
                                    calendar.get(java.util.Calendar.MONTH) + 1,
                                    calendar.get(java.util.Calendar.DAY_OF_MONTH)
                                )
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

