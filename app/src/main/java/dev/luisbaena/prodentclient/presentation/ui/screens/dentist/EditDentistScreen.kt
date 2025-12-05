package dev.luisbaena.prodentclient.presentation.ui.screens.dentist


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import dev.luisbaena.prodentclient.data.remote.dto.dentist.DentistDto
import dev.luisbaena.prodentclient.presentation.ui.components.BottomNavigationBar
import dev.luisbaena.prodentclient.presentation.ui.components.Cabecera
import dev.luisbaena.prodentclient.presentation.ui.components.common.inputs.LabeledSwitch
import dev.luisbaena.prodentclient.presentation.ui.components.common.buttons.PrimaryLoadingButton
import dev.luisbaena.prodentclient.presentation.ui.components.common.buttons.SecondaryButton
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.ErrorCard
import dev.luisbaena.prodentclient.presentation.ui.components.common.dialogs.SuccessDialog
import dev.luisbaena.prodentclient.presentation.ui.components.common.inputs.CustomTextField
import dev.luisbaena.prodentclient.presentation.viewmodel.ClinicViewModel
import dev.luisbaena.prodentclient.presentation.viewmodel.DentistViewModel

    /**
     * Pantalla para editar un dentista existente
     * Permite modificar los datos del dentista y guardarlos
     */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDentistScreen(
    modifier: Modifier = Modifier,
    dentistId: String,
    dentistViewModel: DentistViewModel = hiltViewModel(),
    clinicViewModel: ClinicViewModel = hiltViewModel(),
    navController: NavHostController,
    onOpenDrawer: () -> Unit
) {
    val formState by dentistViewModel.formState.collectAsState()
    val detailState by dentistViewModel.detailState.collectAsState()
    val scrollState = rememberScrollState()

    // Estados del formulario
    var selectedClinicId by rememberSaveable { mutableStateOf("") }
    var selectedClinicName by rememberSaveable { mutableStateOf("") }
    var nombre by rememberSaveable { mutableStateOf("") }
    var apellidos by rememberSaveable { mutableStateOf("") }
    var numeroColegial by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var activa by rememberSaveable { mutableStateOf(true) }

    // Validaciones
    var clinicaError by rememberSaveable { mutableStateOf(false) }
    var nombreError by rememberSaveable { mutableStateOf(false) }
    var apellidosError by rememberSaveable { mutableStateOf(false) }
    var emailError by rememberSaveable { mutableStateOf(false) }

    // Diálogo de éxito
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }

    // Flag para saber si ya se cargaron los datos
    var dataLoaded by rememberSaveable { mutableStateOf(false) }

    // Cargar dentista y clínicas al entrar
    LaunchedEffect(dentistId) {
        dentistViewModel.loadDentistById(dentistId)
        clinicViewModel.loadClinicas()
    }

    // Prellenar el formulario cuando se cargue el dentista
    LaunchedEffect(detailState.dentist) {
        detailState.dentist?.let { dentist ->
            if (!dataLoaded) {
                selectedClinicId = dentist.clinicaId
                selectedClinicName = dentist.clinicaNombre
                nombre = dentist.nombre
                apellidos = dentist.apellidos
                numeroColegial = dentist.numeroColegial ?: ""
                telefono = dentist.telefono ?: ""
                email = dentist.email ?: ""
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
            dentistViewModel.resetFormState()
        }
    }

    Scaffold(
        topBar = {
            Cabecera(
                titulo = "Editar Dentista",
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
                // ESTADO: CARGANDO DENTISTA
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
                            Text("Cargando datos del dentista...")
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
                                onClick = { dentistViewModel.loadDentistById(dentistId) }
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }

                // ESTADO: FORMULARIO CARGADO
                detailState.dentist != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp)
                    ) {

                        Spacer(modifier = Modifier.height(12.dp))

                        // DATOS DEL DENTISTA
                        Text(
                            text = "Datos del dentista",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Nombre
                        CustomTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = "Nombre del dentista",
                            placeholder = "Ej: Juan",
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = Icons.Default.Person
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Apellidos
                        CustomTextField(
                            value = apellidos,
                            onValueChange = { apellidos = it },
                            label = "Apellidos del dentista",
                            placeholder = "Ej: García López",
                            modifier = Modifier.fillMaxWidth(),
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Número Colegial
                        CustomTextField(
                            value = numeroColegial,
                            onValueChange = { numeroColegial = it },
                            label = "Número Colegial (opcional)",
                            placeholder = "Ej: 123456",
                            modifier = Modifier.fillMaxWidth(),
                            keyboardType = KeyboardType.Number
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Teléfono
                        CustomTextField(
                            value = telefono,
                            onValueChange = { telefono = it },
                            label = "Teléfono (opcional)",
                            placeholder = "Ej: 666777888",
                            modifier = Modifier.fillMaxWidth(),
                            keyboardType = KeyboardType.Phone
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Email
                        CustomTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email (opcional)",
                            placeholder = "Ej: ejemplo@ejemplo.com",
                            modifier = Modifier.fillMaxWidth(),
                            keyboardType = KeyboardType.Email,
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // SWITCH ACTIVA/INACTIVA
                        LabeledSwitch(
                            label = "Dentista activo",
                            checked = activa,
                            onCheckedChange = { activa = it }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // MENSAJE DE ERROR
                        if (formState.error != null) {
                            ErrorCard(
                                errorMessage = formState.error!!,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        // BOTÓN ACTUALIZAR
                        PrimaryLoadingButton(
                            text = "Actualizar Dentista",
                            isLoading = formState.isLoading,
                            onClick = {
                                // Validaciones
                                clinicaError = selectedClinicId.isBlank()
                                nombreError = nombre.isBlank()
                                apellidosError = apellidos.isBlank()
                                emailError = email.isNotBlank() &&
                                        !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                                            .matches()

                                if (clinicaError || nombreError || apellidosError || emailError) {
                                    return@PrimaryLoadingButton
                                }

                                // Actualizar dentista
                                val dentist = DentistDto(
                                    id = dentistId,
                                    clinicaId = selectedClinicId,
                                    clinicaNombre = selectedClinicName,
                                    nombre = nombre.trim(),
                                    apellidos = apellidos.trim(),
                                    numeroColegial = numeroColegial.trim().ifBlank { null },
                                    telefono = telefono.trim().ifBlank { null },
                                    email = email.trim().ifBlank { null },
                                    activo = activa,
                                    fechaRegistro = detailState.dentist?.fechaRegistro ?: "",
                                    clinicaTelefono = detailState.dentist?.clinicaTelefono ?: ""
                                )

                                dentistViewModel.updateDentist(dentistId, dentist) { }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(
                                    56.dp
                                ),
                            icon = Icons.Default.Save
                        )
                        Spacer(modifier = Modifier.height(16.dp))

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
    SuccessDialog(
        show = showSuccessDialog,
        title = "¡Dentista actualizado!",
        message = "Los cambios se han guardado correctamente.",
        onConfirm = {
            showSuccessDialog = false
            dentistViewModel.resetFormState()
            navController.popBackStack()
        },
        confirmButtonText = "Aceptar"
    )
}