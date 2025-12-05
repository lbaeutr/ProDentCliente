package dev.luisbaena.prodentclient.presentation.ui.screens.work

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import dev.luisbaena.prodentclient.data.remote.dto.work.WorkRequestDTO
import dev.luisbaena.prodentclient.presentation.ui.components.BottomNavigationBar
import dev.luisbaena.prodentclient.presentation.ui.components.Cabecera
import dev.luisbaena.prodentclient.presentation.ui.components.common.buttons.PrimaryLoadingButton
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.ErrorCard
import dev.luisbaena.prodentclient.presentation.ui.components.common.dialogs.ImageUploadProgressDialog
import dev.luisbaena.prodentclient.presentation.ui.components.common.dialogs.SuccessDialog
import dev.luisbaena.prodentclient.presentation.ui.components.common.inputs.CustomDropdown
import dev.luisbaena.prodentclient.presentation.ui.components.common.inputs.CustomTextField
import dev.luisbaena.prodentclient.presentation.utils.ImageWorkPreviewGrid
import dev.luisbaena.prodentclient.presentation.utils.uploadImageToWork
import dev.luisbaena.prodentclient.presentation.viewmodel.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Screen para crear un nuevo trabajo
 */
@SuppressLint("DefaultLocale") // Para usar uppercase() sin advertencias
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel(),
    workViewModel: WorkViewModel = hiltViewModel(),
    clinicViewModel: ClinicViewModel = hiltViewModel(),
    dentistViewModel: DentistViewModel = hiltViewModel(),
    workTypeViewModel: WorkTypeViewModel = hiltViewModel(),
    materialViewModel: MaterialViewModel = hiltViewModel(),
    navController: NavHostController,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    val formState by workViewModel.formState.collectAsState()
    val scrollState = rememberScrollState()
    val authUiState by authViewModel.uiState.collectAsState()
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
    val isAdmin = authUiState.user?.role?.uppercase() == "ADMIN"

    // Listas para dropdowns
    val clinicsState by clinicViewModel.uiState.collectAsState()
    val dentistsState by dentistViewModel.listState.collectAsState()
    val workTypesState by workTypeViewModel.listState.collectAsState()
    val materialsState by materialViewModel.listState.collectAsState()

    // Estados del formulario - BÁSICOS OBLIGATORIOS
    var numeroTrabajo by rememberSaveable { mutableStateOf("") }
    var selectedClinicaId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedClinicaName by rememberSaveable { mutableStateOf("") }
    var selectedDentistaId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedDentistaName by rememberSaveable { mutableStateOf("") }
    var selectedTipoTrabajoId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedTipoTrabajoName by rememberSaveable { mutableStateOf("") }
    var pacienteNombre by rememberSaveable { mutableStateOf("") }
    var piezasDentales by rememberSaveable { mutableStateOf("") }
    var precio by rememberSaveable { mutableStateOf("") }

    // Estados del formulario - OPCIONALES
    var selectedMaterialId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedMaterialName by rememberSaveable { mutableStateOf("") }
    var color by rememberSaveable { mutableStateOf("") }
    var guiaColor by rememberSaveable { mutableStateOf("") }
    var descripcionTrabajo by rememberSaveable { mutableStateOf("") }
    var observaciones by rememberSaveable { mutableStateOf("") }
    var ajustePrecio by rememberSaveable { mutableStateOf("") }
    var urgente by rememberSaveable { mutableStateOf(false) }
    var fechaEntregaEstimada by rememberSaveable { mutableStateOf<LocalDate?>(null) }

    // Validaciones
    var numeroTrabajoError by rememberSaveable { mutableStateOf(false) }
    var clinicaError by rememberSaveable { mutableStateOf(false) }
    var dentistaError by rememberSaveable { mutableStateOf(false) }
    var tipoTrabajoError by rememberSaveable { mutableStateOf(false) }
    var pacienteError by rememberSaveable { mutableStateOf(false) }
    var precioError by rememberSaveable { mutableStateOf(false) }
    var clinicaInactivaError by rememberSaveable { mutableStateOf(false) }
    var dentistaInactivoError by rememberSaveable { mutableStateOf(false) }
    var tipoTrabajoInactivoError by rememberSaveable { mutableStateOf(false) }

    // Imágenes seleccionadas (Uri no es Parcelable, usar remember normal)
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }

    // Estados para la subida de imágenes
    var showImageUploadDialog by rememberSaveable { mutableStateOf(false) }
    var uploadedImagesCount by rememberSaveable { mutableStateOf(0) }
    var totalImagesToUpload by rememberSaveable { mutableStateOf(0) }
    var uploadError by rememberSaveable { mutableStateOf<String?>(null) }
    var currentWorkId by rememberSaveable { mutableStateOf<String?>(null) }

    // Diálogos
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    // Date picker state
    val datePickerState = rememberDatePickerState()

    // Para subir imágenes
    val imageUploadState by workViewModel.imageUploadState.collectAsState()


    // Cargar datos al entrar
    LaunchedEffect(Unit) {
        clinicViewModel.loadClinicas()
        workTypeViewModel.loadWorkTypes()
        materialViewModel.loadMaterials()
    }

    // Cargar dentistas cuando se selecciona clínica
    LaunchedEffect(selectedClinicaId) {
        selectedClinicaId?.let { clinicaId ->
            dentistViewModel.loadDentists(forceRefresh = true)
            // Reset dentista selection
            selectedDentistaId = null
            selectedDentistaName = ""
        }
    }

    // Observar estado de éxito DEL TRABAJO (no mostrar hasta que imágenes terminen)
    LaunchedEffect(formState.isSuccess) {
        if (formState.isSuccess) {
            // Solo mostrar éxito si NO hay imágenes o ya se subieron todas
            if (selectedImages.isEmpty()) {
                showSuccessDialog = true
            }
        }
    }

    // Observar estado de subida de CADA imagen
    LaunchedEffect(imageUploadState.isSuccess, imageUploadState.isLoading, imageUploadState.error) {
        if (imageUploadState.isSuccess && currentWorkId != null) {
            uploadedImagesCount++
            workViewModel.resetImageUploadState()

            // Si aún quedan imágenes por subir, subir la siguiente
            if (uploadedImagesCount < totalImagesToUpload && uploadedImagesCount < selectedImages.size) {
                uploadImageToWork(
                    context = context,
                    workId = currentWorkId!!,
                    imageUri = selectedImages[uploadedImagesCount],
                    imageType = "GENERAL",
                    description = null,
                    viewModel = workViewModel
                )
            }
            // Si terminamos de subir TODAS las imágenes
            else if (uploadedImagesCount >= totalImagesToUpload) {
                showImageUploadDialog = false
                uploadError = null
                showSuccessDialog = true
            }
        }
    }

    // Observar errores en la subida de imágenes
    LaunchedEffect(imageUploadState.error) {
        imageUploadState.error?.let { error ->
            uploadError = error
            showImageUploadDialog = false
        }
    }

    // Limpiar al salir
    DisposableEffect(Unit) {
        onDispose {
            workViewModel.resetFormState()
        }
    }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        selectedImages = uris
    }

    Scaffold(
        topBar = {
            Cabecera(
                titulo = "Crear Trabajo",
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
            // DATOS BÁSICOS OBLIGATORIOS
            Text(
                text = "Datos Básicos *",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Número de trabajo con escáner QR integrado
            CustomTextField(
                value = numeroTrabajo,
                onValueChange = {
                    numeroTrabajo = it
                    numeroTrabajoError = false
                },
                label = "Número de Trabajo *",
                placeholder = "Ej: 6060",
                leadingIcon = Icons.Default.Numbers,
                isError = numeroTrabajoError,
                keyboardType = KeyboardType.Number,
                errorMessage = "El número de trabajo es obligatorio",
                enableQRScanner = true,
                onQRScanned = { scannedContent ->
                    numeroTrabajo = scannedContent
                    numeroTrabajoError = false
                },
                onQRError = { /* Manejar error silenciosamente */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(numeroTrabajoFocusRequester)
                    .onGloballyPositioned { coordinates ->
                        numeroTrabajoYPosition = coordinates.positionInParent().y
                    }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Dropdown Clínica
            CustomDropdown(
                value = selectedClinicaName,
                items = clinicsState.clinicas,
                onItemSelected = { clinic ->
                    selectedClinicaId = clinic.id
                    selectedClinicaName = clinic.nombre
                    clinicaError = false
                    clinicaInactivaError = false
                },
                itemToString = { it.nombre },
                label = "Clínica *",
                placeholder = "Seleccionar clínica",
                leadingIcon = Icons.Default.Business,
                isLoading = clinicsState.isLoading,
                isError = clinicaError || clinicaInactivaError,
                errorMessage = when {
                    clinicaError -> "Debes seleccionar una clínica"
                    clinicaInactivaError -> "La clínica seleccionada está inactiva"
                    else -> ""
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        clinicaYPosition = coordinates.positionInParent().y
                    }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Dropdown Dentista (filtrado por clínica)
            CustomDropdown(
                value = selectedDentistaName,
                items = dentistsState.dentists.filter { dentist ->
                    dentist.clinicaNombre == selectedClinicaName
                },
                onItemSelected = { dentist ->
                    selectedDentistaId = dentist.id
                    selectedDentistaName = "${dentist.nombre} ${dentist.apellidos}"
                    dentistaError = false
                    dentistaInactivoError = false
                },
                itemToString = { "${it.nombre} ${it.apellidos}" },
                label = "Dentista *",
                placeholder = if (selectedClinicaId == null)
                    "Primero selecciona una clínica"
                else
                    "Seleccionar dentista",
                leadingIcon = Icons.Default.PersonOutline,
                isLoading = dentistsState.isLoading,
                isError = dentistaError || dentistaInactivoError,
                errorMessage = when {
                    dentistaError -> "Debes seleccionar un dentista"
                    dentistaInactivoError -> "El dentista seleccionado está inactivo"
                    else -> ""
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        dentistaYPosition = coordinates.positionInParent().y
                    },
                enabled = selectedClinicaId != null
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Dropdown Tipo de Trabajo
            CustomDropdown(
                value = selectedTipoTrabajoName,
                items = workTypesState.workTypes,
                onItemSelected = { type ->
                    selectedTipoTrabajoId = type.id
                    selectedTipoTrabajoName = type.nombre
                    tipoTrabajoError = false
                    tipoTrabajoInactivoError = false
                    // Auto-rellenar precio
                    type.precioBase?.let { precioBase ->
                        // Si es admin y el precio está vacío, lo rellena
                        // Si NO es admin, SIEMPRE establece el precio del tipo de trabajo
                        if (!isAdmin || precio.isBlank()) {
                            // Usar Locale.US para asegurar formato con punto decimal
                            precio = String.format(java.util.Locale.US, "%.2f", precioBase)
                        }
                    }
                },
                itemToString = { it.nombre },
                label = "Tipo de Trabajo *",
                placeholder = "Seleccionar tipo",
                leadingIcon = Icons.Default.Work,
                isLoading = workTypesState.isLoading,
                isError = tipoTrabajoError || tipoTrabajoInactivoError,
                errorMessage = when {
                    tipoTrabajoError -> "Debes seleccionar un tipo de trabajo"
                    tipoTrabajoInactivoError -> "El tipo de trabajo seleccionado está inactivo"
                    else -> ""
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        tipoTrabajoYPosition = coordinates.positionInParent().y
                    }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Paciente

            CustomTextField(
                value = pacienteNombre,
                onValueChange = {
                    pacienteNombre = it
                    pacienteError = false
                },
                label = "Nombre del Paciente *",
                placeholder = "Ej: Juan Pérez",
                leadingIcon = Icons.Default.Person,
                isError = pacienteError,
                keyboardType = KeyboardType.Text,
                errorMessage = "El nombre del paciente es obligatorio",
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(pacienteFocusRequester)
                    .onGloballyPositioned { coordinates ->
                        pacienteYPosition = coordinates.positionInParent().y
                    }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Piezas dentales
            //TODO: pintar imagenes para un mejor entendimiento, lo pide el cliente
            CustomTextField(
                value = piezasDentales,
                onValueChange = {
                    piezasDentales = it
                },
                label = "Piezas Dentales *",
                placeholder = "Ej: 11, 12, 13",
                leadingIcon = Icons.Default.LocalHospital,
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (isAdmin) {
                // Precio
                CustomTextField(
                    value = precio,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                            precio = it
                            precioError = false
                        }
                    },
                    label = "Precio *",
                    placeholder = "Ej: 150.00",
                    leadingIcon = Icons.Default.Euro,
                    suffix = { Text("€") },
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
            }

            Spacer(modifier = Modifier.height(24.dp))

            // DATOS OPCIONALES
            Text(
                text = "Datos Opcionales",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Dropdown Material
            CustomDropdown(
                value = selectedMaterialName,
                items = materialsState.materials,
                onItemSelected = { material ->
                    selectedMaterialId = material.id
                    selectedMaterialName = material.nombre
                },
                itemToString = { it.nombre },
                label = "Material (opcional)",
                placeholder = "Seleccionar material",
                leadingIcon = Icons.Default.Science,
                isLoading = materialsState.isLoading,
                modifier = Modifier.fillMaxWidth(),
                allowClear = true,
                onClear = {
                    selectedMaterialId = null
                    selectedMaterialName = ""
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Color
            CustomTextField(
                value = color,
                onValueChange = { color = it },
                label = "Color (opcional)",
                placeholder = "Ej: A2",
                leadingIcon = Icons.Default.Palette,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Guía de color
            CustomTextField(
                value = guiaColor,
                onValueChange = { guiaColor = it },
                label = "Guía de Color (opcional)",
                placeholder = "Ej: Vita Classical",
                leadingIcon = Icons.Default.ColorLens,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Descripción del trabajo
            CustomTextField(
                value = descripcionTrabajo,
                onValueChange = { descripcionTrabajo = it },
                label = "Descripción del Trabajo (opcional)",
                placeholder = "Ej: Corona con características especiales...",
                leadingIcon = Icons.Default.Description,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Observaciones
            CustomTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                label = "Observaciones (opcional)",
                placeholder = "Notas adicionales...",
                leadingIcon = Icons.AutoMirrored.Filled.Notes,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Ajuste de precio
            if (isAdmin) {
                CustomTextField(
                    value = ajustePrecio,
                    onValueChange = { ajustePrecio = it },
                    label = "Motivo Ajuste Precio (opcional)",
                    placeholder = "Ej: Descuento por fidelidad",
                    leadingIcon = Icons.Default.MoneyOff,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Switch Urgente
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

            Spacer(modifier = Modifier.height(12.dp))

            // Fecha de entrega estimada
            CustomTextField(
                value = fechaEntregaEstimada?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    ?: "",
                onValueChange = {},
                label = "Fecha Entrega Estimada (opcional)",
                placeholder = "Seleccionar fecha",
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha")
                    }
                },
                leadingIcon = Icons.Default.Event,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))


            // IMÁGENES (OPCIONAL)
            Text(
                text = "Imágenes (Opcional)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Botón para seleccionar imágenes
            OutlinedButton(
                onClick = {
                    imagePickerLauncher.launch("image/*")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Seleccionar Imágenes")
            }

            // Preview de imágenes seleccionadas
            if (selectedImages.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${selectedImages.size} imagen(es) seleccionada(s)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Grid de imágenes
                ImageWorkPreviewGrid(
                    images = selectedImages,
                    onRemoveImage = { uri ->
                        selectedImages = selectedImages.filter { it != uri }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // MENSAJE DE ERROR GENERAL
            if (formState.error != null) {
                ErrorCard(
                    errorMessage = formState.error!!,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }


            // BOTÓN CREAR TRABAJO
            PrimaryLoadingButton(
                text = "Crear Trabajo",
                isLoading = formState.isLoading,
                onClick = {
                    // Validaciones básicas
                    numeroTrabajoError = numeroTrabajo.isBlank()
                    clinicaError = selectedClinicaId == null
                    dentistaError = selectedDentistaId == null
                    tipoTrabajoError = selectedTipoTrabajoId == null
                    pacienteError = pacienteNombre.isBlank()

                    // Verificar si la clínica está activa
                    clinicaInactivaError = false
                    if (selectedClinicaId != null) {
                        val selectedClinic = clinicsState.clinicas.find { it.id == selectedClinicaId }
                        if (selectedClinic != null && !selectedClinic.activa) {
                            clinicaInactivaError = true
                        }
                    }

                    // Verificar si el dentista está activo
                    dentistaInactivoError = false
                    if (selectedDentistaId != null) {
                        val selectedDentist = dentistsState.dentists.find { it.id == selectedDentistaId }
                        if (selectedDentist != null && !selectedDentist.activo) {
                            dentistaInactivoError = true
                        }
                    }

                    // Verificar si el tipo de trabajo está activo
                    tipoTrabajoInactivoError = false
                    if (selectedTipoTrabajoId != null) {
                        val selectedWorkType = workTypesState.workTypes.find { it.id == selectedTipoTrabajoId }
                        if (selectedWorkType != null && !selectedWorkType.activo) {
                            tipoTrabajoInactivoError = true
                        }
                    }

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
                        tipoTrabajoError || pacienteError || precioError ||
                        clinicaInactivaError || dentistaInactivoError || tipoTrabajoInactivoError
                    ) {
                        // Hacer focus y scroll al primer campo con error
                        coroutineScope.launch {
                            val targetPosition = when {
                                numeroTrabajoError -> {
                                    numeroTrabajoFocusRequester.requestFocus()
                                    numeroTrabajoYPosition
                                }

                                clinicaError || clinicaInactivaError -> clinicaYPosition
                                dentistaError || dentistaInactivoError -> dentistaYPosition
                                tipoTrabajoError || tipoTrabajoInactivoError -> tipoTrabajoYPosition
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
                            scrollState.animateScrollTo(
                                (targetPosition - offset).toInt().coerceAtLeast(0)
                            )
                        }
                        return@PrimaryLoadingButton
                    }

                    // Determinar el precio a enviar
                    val precioFinal = try {
                        if (precio.isNotBlank()) {
                            // Reemplazar coma por punto para asegurar conversión correcta
                            precio.replace(",", ".").toDouble()
                        } else {
                            0.0 // Si no hay precio, usar 0.0
                        }
                    } catch (_: Exception) {
                        0.0
                    }

                    // Crear DTO
                    val work = WorkRequestDTO(
                        numeroTrabajo = numeroTrabajo.trim(),
                        clinicaId = selectedClinicaId!!,
                        dentistaId = selectedDentistaId!!,
                        tipoTrabajoId = selectedTipoTrabajoId!!,
                        materialId = selectedMaterialId,
                        pacienteNombre = pacienteNombre.trim(),
                        piezasDentales = piezasDentales.trim(),
                        color = color.trim().ifBlank { null },
                        guiaColor = guiaColor.trim().ifBlank { null },
                        descripcionTrabajo = descripcionTrabajo.trim().ifBlank { null },
                        observaciones = observaciones.trim().ifBlank { null },
                        precio = precioFinal,
                        ajustePrecio = ajustePrecio.trim().ifBlank { null },
                        urgente = urgente,
                        fechaEntregaEstimada = fechaEntregaEstimada?.toString(),
                        protesicoId = null
                    )


                    //  Crear trabajo y subir imágenes
                    workViewModel.createWork(work) { createdWorkId ->
                        currentWorkId = createdWorkId

                        // Si hay imágenes seleccionadas
                        if (selectedImages.isNotEmpty()) {
                            // Reiniciar contadores
                            uploadedImagesCount = 0
                            totalImagesToUpload = selectedImages.size
                            uploadError = null
                            showImageUploadDialog = true

                            // Subir la primera imagen (las demás se suben secuencialmente en el LaunchedEffect)
                            uploadImageToWork(
                                context = context,
                                workId = createdWorkId,
                                imageUri = selectedImages[0],
                                imageType = "GENERAL",
                                description = null,
                                viewModel = workViewModel
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)

            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }


    // DIÁLOGO DE ÉXITO
    if (showSuccessDialog) {
        SuccessDialog(
            show = showSuccessDialog,
            title = "¡Trabajo creado!",
            message = "El trabajo ha sido creado exitosamente.\nNúmero: $numeroTrabajo\nPaciente: $pacienteNombre" +
                    if (selectedImages.isNotEmpty()) "\n${selectedImages.size} imagen(es) subidas" else "",
            onConfirm = {
                showSuccessDialog = false
                workViewModel.resetFormState()
                navController.popBackStack()
            }
        )
    }


// DIÁLOGO DE PROGRESO DE SUBIDA DE IMÁGENES
    if (showImageUploadDialog) {
        ImageUploadProgressDialog(
            uploadedCount = uploadedImagesCount,
            totalCount = totalImagesToUpload,
            error = uploadError,
            onDismiss = {
                showImageUploadDialog = false
                uploadError = null
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
                            fechaEntregaEstimada = java.time.Instant
                                .ofEpochMilli(millis)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate()
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
