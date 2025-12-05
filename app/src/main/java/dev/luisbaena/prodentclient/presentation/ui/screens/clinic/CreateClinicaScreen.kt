package dev.luisbaena.prodentclient.presentation.ui.screens.clinic

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
import dev.luisbaena.prodentclient.presentation.ui.components.BottomNavigationBar
import dev.luisbaena.prodentclient.presentation.ui.components.Cabecera
import dev.luisbaena.prodentclient.presentation.ui.components.common.buttons.PrimaryLoadingButton
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.ErrorCard
import dev.luisbaena.prodentclient.presentation.ui.components.common.cards.InfoCard
import dev.luisbaena.prodentclient.presentation.ui.components.common.dialogs.SuccessDialog
import dev.luisbaena.prodentclient.presentation.ui.components.common.inputs.CustomTextField
import dev.luisbaena.prodentclient.presentation.viewmodel.ClinicViewModel
import java.text.SimpleDateFormat
import java.util.*

    /**
     * Pantalla para crear una nueva clínica
     * Incluye un formulario con validaciones y manejo de estado
     */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateClinicaScreen(
    modifier: Modifier = Modifier,
    viewModel: ClinicViewModel = hiltViewModel(),
    navController: NavHostController,
    onOpenDrawer: () -> Unit
) {
    val formState by viewModel.formState.collectAsState()
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
    var horaLunes by rememberSaveable { mutableStateOf("9:00-18:00") }
    var horaMartes by rememberSaveable { mutableStateOf("9:00-18:00") }
    var horaMiercoles by rememberSaveable { mutableStateOf("9:00-18:00") }
    var horaJueves by rememberSaveable { mutableStateOf("9:00-18:00") }
    var horaViernes by rememberSaveable { mutableStateOf("9:00-15:00") }

    // Validaciones
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

    LaunchedEffect(formState.isSuccess) {
        if (formState.isSuccess) {
            showSuccessDialog = true
        }
    }

    Scaffold(
        topBar = {
            Cabecera(
                titulo = "Nueva Clínica",
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
                message = "Completa el formulario para crear una nueva clínica."
            )
            Spacer(modifier = Modifier.height(12.dp))

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
                label = "Nombre de la clínica",
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
                label = "Dirección",
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
                label = "Teléfono",
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
                leadingIcon = Icons.Default.Note,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Switch activa/inactiva
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Clínica activa",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Switch(
                    checked = activa,
                    onCheckedChange = { activa = it }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // MENSAJE DE ERROR
            if (formState.error != null) {
                ErrorCard(
                    errorMessage = formState.error!!,
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            Modifier
                        )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryLoadingButton(
                text = "Crear Clínica",
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

                    // Crear clínica
                    val clinica = Clinic(
                        id = "",
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
                        fechaRegistro = SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss",
                            Locale.getDefault()
                        ).format(Date()),
                        observaciones = observaciones.trim().ifBlank { null }
                    )

                    viewModel.createClinica(clinica) { }
                },
                isLoading = formState.isLoading,
                loadingText = "Creando clínica...",
                icon = Icons.Default.AddBusiness,
                enabled = !formState.isLoading
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
    // DIÁLOGO DE ÉXITO
    SuccessDialog(
        show = showSuccessDialog,
        title = "¡Clínica creada!",
        message = "La clínica ha sido creada exitosamente.",
        onConfirm = {
            showSuccessDialog = false
            viewModel.resetFormState()
            navController.popBackStack()
        },
        icon = Icons.Default.CheckCircle,
        confirmButtonText = "Aceptar"
    )
}