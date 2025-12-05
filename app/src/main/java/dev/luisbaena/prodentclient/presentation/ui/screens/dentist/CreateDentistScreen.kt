package dev.luisbaena.prodentclient.presentation.ui.screens.dentist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import dev.luisbaena.prodentclient.data.remote.dto.dentist.DentistCreateRequestDTO
import dev.luisbaena.prodentclient.presentation.ui.components.BottomNavigationBar
import dev.luisbaena.prodentclient.presentation.ui.components.Cabecera
import dev.luisbaena.prodentclient.presentation.ui.components.common.buttons.PrimaryLoadingButton
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.ErrorCard
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.InfoCard
import dev.luisbaena.prodentclient.presentation.ui.components.common.dialogs.SuccessDialog
import dev.luisbaena.prodentclient.presentation.ui.components.common.inputs.CustomDropdown
import dev.luisbaena.prodentclient.presentation.ui.components.common.inputs.CustomTextField
import dev.luisbaena.prodentclient.presentation.viewmodel.ClinicViewModel
import dev.luisbaena.prodentclient.presentation.viewmodel.DentistViewModel
import kotlinx.coroutines.launch

    /**
     * Pantalla para crear un nuevo dentista
     * Permite seleccionar la clínica a la que pertenecerá el dentista
     * y completar los datos necesarios para su creación
     */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDentistScreen(
    modifier: Modifier = Modifier,
    dentistViewModel: DentistViewModel = hiltViewModel(),
    clinicViewModel: ClinicViewModel = hiltViewModel(),
    navController: NavHostController,
    onOpenDrawer: () -> Unit
) {
    val formState by dentistViewModel.formState.collectAsState()
    val clinicListState by clinicViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    // FocusRequesters para campos obligatorios
    val nombreFocusRequester = remember { FocusRequester() }
    val apellidosFocusRequester = remember { FocusRequester() }

    // Estados del formulario
    var selectedClinicId by rememberSaveable { mutableStateOf("") }
    var selectedClinicName by rememberSaveable { mutableStateOf("") }
    var nombre by rememberSaveable { mutableStateOf("") }
    var apellidos by rememberSaveable { mutableStateOf("") }
    var numeroColegial by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }


    // Validaciones
    var clinicaError by rememberSaveable { mutableStateOf(false) }
    var nombreError by rememberSaveable { mutableStateOf(false) }
    var apellidosError by rememberSaveable { mutableStateOf(false) }
    var emailError by rememberSaveable { mutableStateOf(false) }

    // Diálogo de éxito
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }

    // Cargar clínicas al entrar
    LaunchedEffect(Unit) {
        clinicViewModel.loadClinicas()
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
            dentistViewModel.resetFormState()
        }
    }

    Scaffold(
        topBar = {
            Cabecera(
                titulo = "Crear Dentista",
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
            InfoCard(
                message = "Aquí puedes crear un nuevo dentista asignándolo a una clínica existente.",
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Default.Info
            )

            Spacer( modifier = Modifier.height(24.dp))

            // SELECCIONAR CLÍNICA
            Text(
                text = "Clínica",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Selecciona la clínica a la que pertenecerá el dentista:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Spacer( modifier = Modifier.height(8.dp))

            // Dropdown Dentista
            CustomDropdown(
                value = selectedClinicName,
                label = "Seleccionar clínica *",
                placeholder = "Selecciona una clínica",
                items = clinicListState.filteredClinicas,
                onItemSelected = { clinica ->
                    selectedClinicId = clinica.id
                    selectedClinicName = clinica.nombre
                    clinicaError = false
                },
                itemToString = { it.nombre },
                isLoading = clinicListState.isLoading,
                isError = clinicaError,
                errorMessage = "Debes seleccionar una clínica",
                emptyMessage = "No hay clínicas disponibles",
                leadingIcon = Icons.Default.Business,
                enabled = true,
                modifier = Modifier.fillMaxWidth(),
                onRetry = { clinicViewModel.loadClinicas(forceRefresh = true) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // DATOS DEL DENTISTA
            Text(
                text = "Datos del dentista",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Completa los datos del dentista a crear:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Spacer( modifier = Modifier.height(8.dp))

            // Nombre
            CustomTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    nombreError = false
                },
                label = "Nombre *",
                placeholder = "Ej: Juan",
                isError = nombreError,
                modifier = Modifier.fillMaxWidth(),
                errorMessage = "El nombre es obligatorio",
                focusRequester = nombreFocusRequester
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Apellidos
            CustomTextField(
                value = apellidos,
                onValueChange = {
                    apellidos = it
                    apellidosError = false
                },
                label = "Apellidos *",
                placeholder = "Ej: García López",
                isError = apellidosError,
                modifier = Modifier.fillMaxWidth(),
                errorMessage = "Los apellidos son obligatorios",
                focusRequester = apellidosFocusRequester
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Número Colegial
            CustomTextField(
                value = numeroColegial,
                onValueChange = { numeroColegial = it },
                label = "Número Colegial (opcional)",
                modifier = Modifier.fillMaxWidth(),
                placeholder = "Ej: 123456",
                leadingIcon = Icons.Default.Badge
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Teléfono
            CustomTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = "Teléfono (opcional)",
                placeholder = "Ej: 666777888",
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Phone,
                leadingIcon = Icons.Default.Phone
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Email
            CustomTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = false
                },
                label = "Email (opcional)",
                keyboardType = KeyboardType.Email,
                modifier = Modifier.fillMaxWidth(),
                isError = emailError,
                errorMessage = "Email inválido",
                placeholder = "Ej: dentista@ejemplo.com",
                leadingIcon = Icons.Default.Email
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ============================================
            // MENSAJE DE ERROR
            // ============================================
            if (formState.error != null) {
                ErrorCard(
                    errorMessage = formState.error!!,
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            Modifier
                        )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // BOTÓN CREAR
            PrimaryLoadingButton(
                text = "Crear dentista",
                onClick = {
                    // Validaciones
                    clinicaError = selectedClinicId.isBlank()
                    nombreError = nombre.isBlank()
                    apellidosError = apellidos.isBlank()
                    emailError = email.isNotBlank() &&
                            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

                    // Si hay errores, hacer focus al primer campo con error
                    if (clinicaError || nombreError || apellidosError || emailError) {
                        coroutineScope.launch {
                            when {
                                nombreError -> {
                                    nombreFocusRequester.requestFocus()
                                    // Scroll al campo nombre (aproximadamente línea 220-240)
                                    scrollState.animateScrollTo(600)
                                }
                                apellidosError -> {
                                    apellidosFocusRequester.requestFocus()
                                    // Scroll al campo apellidos
                                    scrollState.animateScrollTo(700)
                                }
                                clinicaError -> {
                                    // Scroll al dropdown de clínica (al inicio)
                                    scrollState.animateScrollTo(0)
                                }
                            }
                        }
                        return@PrimaryLoadingButton
                    }

                    // Limpiar el foco antes de crear
                    focusManager.clearFocus()

                    // Crear dentista
                    val dentist = DentistCreateRequestDTO(
                        clinicaId = selectedClinicId,
                        nombre = nombre.trim(),
                        apellidos = apellidos.trim(),
                        numeroColegial = numeroColegial.trim(),
                        telefono = telefono.trim(),
                        email = email.trim()
                    )

                    dentistViewModel.createDentist(dentist) { }
                },
                isLoading = formState.isLoading,
                loadingText = "Creando dentista...",
                icon = Icons.Default.Person,
                enabled = !formState.isLoading
            )
            Spacer(modifier = Modifier.height(32.dp))

        }
    }

    // ============================================
    // DIÁLOGO DE ÉXITO
    // ============================================
    if (showSuccessDialog) {
        SuccessDialog(
            show = showSuccessDialog,
            title = "¡Dentista creado!",
            message = "El dentista ha sido creado exitosamente.",
            onConfirm = {
                showSuccessDialog = false
                dentistViewModel.resetFormState()
                navController.popBackStack()
            }
        )
    }
}