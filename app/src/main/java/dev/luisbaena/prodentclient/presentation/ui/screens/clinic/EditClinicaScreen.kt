package dev.luisbaena.prodentclient.presentation.ui.screens.clinic

import dev.luisbaena.prodentclient.presentation.ui.components.BottomNavigationBar
import dev.luisbaena.prodentclient.presentation.ui.components.Cabecera
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import dev.luisbaena.prodentclient.domain.model.Clinic
import dev.luisbaena.prodentclient.domain.model.Horarios
import dev.luisbaena.prodentclient.presentation.ui.components.common.inputs.LabeledSwitch
import dev.luisbaena.prodentclient.presentation.ui.components.common.buttons.PrimaryLoadingButton
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.ErrorCard
import dev.luisbaena.prodentclient.presentation.ui.components.common.dialogs.SuccessDialog
import dev.luisbaena.prodentclient.presentation.ui.components.common.inputs.CustomTextField
import dev.luisbaena.prodentclient.presentation.viewmodel.ClinicViewModel

    /**
     * Pantalla para editar una clínica existente
     * Permite modificar los datos de la clínica con validaciones
     */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditClinicaScreen(
    clinicaId: String,
    modifier: Modifier = Modifier,
    viewModel: ClinicViewModel = hiltViewModel(),
    navController: NavHostController,
    onOpenDrawer: () -> Unit
) {
    val formState by viewModel.formState.collectAsState()
    val detailState by viewModel.detailState.collectAsState()
    val scrollState = rememberScrollState()

    // FocusRequesters para cada campo
    val nombreFocusRequester = remember { FocusRequester() }
    val direccionFocusRequester = remember { FocusRequester() }
    val telefonoFocusRequester = remember { FocusRequester() }

    // Estados del formulario
    var nombre by rememberSaveable { mutableStateOf("") }
    var direccion by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var observaciones by rememberSaveable { mutableStateOf("") }
    var activa by rememberSaveable { mutableStateOf(true) }

    // Horarios
    var horaLunes by rememberSaveable { mutableStateOf("") }
    var horaMartes by rememberSaveable { mutableStateOf("") }
    var horaMiercoles by rememberSaveable { mutableStateOf("") }
    var horaJueves by rememberSaveable { mutableStateOf("") }
    var horaViernes by rememberSaveable { mutableStateOf("") }

    // Estados de error
    var nombreError by rememberSaveable { mutableStateOf(false) }
    var direccionError by rememberSaveable { mutableStateOf(false) }
    var telefonoError by rememberSaveable { mutableStateOf(false) }

    // Diálogo de éxito
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }

    // Estado para controlar cuando hacer focus
    var requestFocusOn by remember { mutableStateOf<FocusRequester?>(null) }
    var scrollToError by remember { mutableStateOf(false) }

    // LaunchedEffect para hacer focus y scroll cuando hay error
    LaunchedEffect(requestFocusOn, scrollToError) {
        if (scrollToError && requestFocusOn != null) {
            // Primero hacer scroll al principio donde están los campos
            scrollState.animateScrollTo(0)
            kotlinx.coroutines.delay(100) // Pequeño delay para asegurar que la UI esté renderizada
            requestFocusOn?.requestFocus()
            requestFocusOn = null
            scrollToError = false
        }
    }

    // Cargar clínica al entrar
    LaunchedEffect(clinicaId) {
        viewModel.loadClinicaById(clinicaId)
    }

    // Prellenar formulario cuando se carga la clínica
    LaunchedEffect(detailState.clinica) {
        detailState.clinica?.let { clinica ->
            nombre = clinica.nombre
            direccion = clinica.direccion
            telefono = clinica.telefono
            email = clinica.email
            observaciones = clinica.observaciones ?: ""
            activa = clinica.activa
            horaLunes = clinica.horarios?.lunes ?: ""
            horaMartes = clinica.horarios?.martes ?: ""
            horaMiercoles = clinica.horarios?.miercoles ?: ""
            horaJueves = clinica.horarios?.jueves ?: ""
            horaViernes = clinica.horarios?.viernes ?: ""
        }
    }

    // Observar estado del formulario
    LaunchedEffect(formState.isSuccess) {
        if (formState.isSuccess) {
            showSuccessDialog = true
        }
    }

    // Limpiar al salir
    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetFormState()
            viewModel.resetDetailState()
        }
    }

    Scaffold(
        topBar = {
            Cabecera(
                titulo = "Editar Clínica",
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

                // ERROR AL CARGAR
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
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadClinicaById(clinicaId) }) {
                                Text("Reintentar")
                            }
                        }
                    }
                }

                // FORMULARIO
                detailState.clinica != null -> {
                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp)
                    ) {
                        // INFORMACIÓN BÁSICA
                        Text(
                            text = "Información básica",
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
                            label = "Nombre de la clínica *",
                            placeholder = "Ej: Clínica Dental San Fernando",
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(nombreFocusRequester),
                            leadingIcon = Icons.Default.Business,
                            isError = nombreError,
                            errorMessage = "El nombre es obligatorio"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Dirección
                        CustomTextField(
                            value = direccion,
                            onValueChange = {
                                direccion = it
                                direccionError = false
                            },
                            label = "Dirección *",
                            placeholder = "Ej: Avda. Al-Andalus nº 13",
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(direccionFocusRequester),
                            leadingIcon = Icons.Default.LocationOn,
                            isError = direccionError,
                            errorMessage = "La dirección es obligatoria"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Teléfono
                        CustomTextField(
                            value = telefono,
                            onValueChange = {
                                telefono = it
                                telefonoError = false
                            },
                            label = "Teléfono *",
                            placeholder = "Ej: 123456789",
                            keyboardType = KeyboardType.Phone,
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(telefonoFocusRequester),
                            leadingIcon = Icons.Default.Phone,
                            isError = telefonoError,
                            errorMessage = "El teléfono es obligatorio"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Email
                        CustomTextField(
                            value = email,
                            onValueChange = {
                                email = it
                            },
                            label = "Email",
                            placeholder = "Ej: clinica@ejemplo.com",
                            keyboardType = KeyboardType.Email,
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = Icons.Default.Email
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // HORARIOS
                        Text(
                            text = "Horarios de atención",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CustomTextField(
                            value = horaLunes,
                            onValueChange = {
                                horaLunes = it
                            },
                            label = "Lunes",
                            placeholder = "9:00-18:00",
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = Icons.Default.Schedule,
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CustomTextField(
                            value = horaMartes,
                            onValueChange = {
                                horaMartes = it
                            },
                            label = "Martes",
                            placeholder = "9:00-18:00",
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = Icons.Default.Schedule,
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CustomTextField(
                            value = horaMiercoles,
                            onValueChange = {
                                horaMiercoles = it
                            },
                            label = "Miércoles",
                            placeholder = "9:00-18:00",
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = Icons.Default.Schedule,
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CustomTextField(
                            value = horaJueves,
                            onValueChange = {
                                horaJueves = it
                            },
                            label = "Jueves",
                            placeholder = "9:00-18:00",
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = Icons.Default.Schedule,
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        CustomTextField(
                            value = horaViernes,
                            onValueChange = {
                                horaViernes = it
                            },
                            label = "Viernes",
                            placeholder = "9:00-15:00",
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = Icons.Default.Schedule,
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // INFORMACIÓN ADICIONAL
                        Text(
                            text = "Información adicional",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Observaciones
                        CustomTextField(
                            value = observaciones,
                            onValueChange = {
                                observaciones = it
                            },
                            label = "Observaciones (opcional)",
                            placeholder = "Notas adicionales...",
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = Icons.Default.Notes,
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Switch activa/inactiva
                        LabeledSwitch(
                            label = "Clínica activa",
                            checked = activa,
                            onCheckedChange = { activa = it },
                            modifier = Modifier.fillMaxWidth(
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // MENSAJE DE ERROR DEL BACKEND
                        if (formState.error != null) {
                            ErrorCard(
                                errorMessage = formState.error!!,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // BOTÓN ACTUALIZAR
                        PrimaryLoadingButton(
                            text = "Actualizar Clínica",
                            onClick = {
                                // Validaciones
                                nombreError = nombre.isBlank()
                                direccionError = direccion.isBlank()
                                telefonoError = telefono.isBlank()

                                if (nombreError || direccionError || telefonoError) {
                                    // Hacer focus y scroll en el primer campo con error
                                    requestFocusOn = when {
                                        nombreError -> nombreFocusRequester
                                        direccionError -> direccionFocusRequester
                                        telefonoError -> telefonoFocusRequester
                                        else -> null
                                    }
                                    scrollToError = true
                                    return@PrimaryLoadingButton
                                }

                                // Actualizar clínica (usando modelo de dominio)
                                val clinicaActualizada = Clinic(
                                    id = clinicaId,
                                    nombre = nombre.trim(),
                                    direccion = direccion.trim(),
                                    telefono = telefono.trim(),
                                    email = email.trim(),
                                    horarios = Horarios(
                                        lunes = horaLunes.trim().ifBlank { null },
                                        martes = horaMartes.trim().ifBlank { null },
                                        miercoles = horaMiercoles.trim().ifBlank { null },
                                        jueves = horaJueves.trim().ifBlank { null },
                                        viernes = horaViernes.trim().ifBlank { null }
                                    ),
                                    activa = activa,
                                    fechaRegistro = detailState.clinica?.fechaRegistro,
                                    observaciones = observaciones.trim().ifBlank { null }
                                )

                                viewModel.updateClinica(clinicaId, clinicaActualizada) {
                                }
                            },
                            isLoading = formState.isLoading,
                            loadingText = "Actualizando...",
                            icon = Icons.Default.SaveAs,
                            enabled = !formState.isLoading
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
    SuccessDialog(
        show = showSuccessDialog,
        title = "¡Clínica actualizada!",
        message = "La clínica ha sido actualizada exitosamente.",
        onConfirm = {
            showSuccessDialog = false
            viewModel.resetFormState()
            navController.popBackStack()
        }
    )
}