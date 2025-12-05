package dev.luisbaena.prodentclient.presentation.ui.example

/**
 * ============================================
 * EJEMPLOS DE USO DE CUSTOMDROPDOWN
 * ============================================
 *
 * Este archivo contiene ejemplos de cómo usar CustomDropdown en diferentes escenarios
 */

/*
// ============================================
// EJEMPLO 1: Uso básico con lista simple
// ============================================

data class City(val id: String, val name: String)

@Composable
fun SimpleDropdownExample() {
    var selectedCityName by remember { mutableStateOf("") }

    val cities = listOf(
        City("1", "Madrid"),
        City("2", "Barcelona"),
        City("3", "Valencia")
    )

    CustomDropdown(
        value = selectedCityName,
        label = "Seleccionar ciudad",
        placeholder = "Elige una ciudad",
        items = cities,
        onItemSelected = { city ->
            selectedCityName = city.name
        },
        itemToString = { it.name },
        leadingIcon = Icons.Default.LocationCity,
        modifier = Modifier.fillMaxWidth()
    )
}

// ============================================
// EJEMPLO 2: Con estado de carga desde ViewModel
// ============================================

@Composable
fun DropdownWithLoadingExample(
    viewModel: MyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedItemName by remember { mutableStateOf("") }

    // Cargar items al iniciar
    LaunchedEffect(Unit) {
        viewModel.loadItems()
    }

    CustomDropdown(
        value = selectedItemName,
        label = "Seleccionar elemento",
        placeholder = "Selecciona una opción",
        items = uiState.items,
        onItemSelected = { item ->
            selectedItemName = item.name
            viewModel.onItemSelected(item)
        },
        itemToString = { it.name },
        isLoading = uiState.isLoading,
        isError = uiState.error != null,
        errorMessage = uiState.error,
        emptyMessage = "No hay elementos disponibles",
        leadingIcon = Icons.Default.List,
        enabled = !uiState.isLoading,
        modifier = Modifier.fillMaxWidth(),
        onRetry = { viewModel.loadItems(forceRefresh = true) }
    )
}

// ============================================
// EJEMPLO 3: Con validación de formulario
// ============================================

@Composable
fun DropdownWithValidationExample() {
    var selectedId by remember { mutableStateOf("") }
    var selectedName by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    val options = listOf(
        Option("1", "Opción 1"),
        Option("2", "Opción 2"),
        Option("3", "Opción 3")
    )

    Column {
        CustomDropdown(
            value = selectedName,
            label = "Campo obligatorio *",
            placeholder = "Selecciona...",
            items = options,
            onItemSelected = { option ->
                selectedId = option.id
                selectedName = option.name
                hasError = false // Limpiar error al seleccionar
            },
            itemToString = { it.name },
            isError = hasError,
            errorMessage = "Este campo es obligatorio",
            leadingIcon = Icons.Default.CheckCircle,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Validar antes de enviar
                if (selectedId.isBlank()) {
                    hasError = true
                } else {
                    // Enviar formulario
                }
            }
        ) {
            Text("Enviar")
        }
    }
}

// ============================================
// EJEMPLO 4: Con contenido personalizado
// ============================================

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String
)

@Composable
fun DropdownWithCustomContentExample() {
    var selectedUserName by remember { mutableStateOf("") }

    val users = listOf(
        User("1", "Juan Pérez", "juan@example.com", "Admin"),
        User("2", "María García", "maria@example.com", "User"),
        User("3", "Pedro López", "pedro@example.com", "User")
    )

    CustomDropdown(
        value = selectedUserName,
        label = "Seleccionar usuario",
        placeholder = "Buscar usuario...",
        items = users,
        onItemSelected = { user ->
            selectedUserName = user.name
        },
        itemToString = { it.name },
        leadingIcon = Icons.Default.Person,
        modifier = Modifier.fillMaxWidth(),
        // Contenido personalizado para cada item
        itemContent = { user ->
            Column {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "Rol: ${user.role}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

// ============================================
// EJEMPLO 5: Dropdown múltiples en cascada
// ============================================

@Composable
fun CascadeDropdownExample() {
    var selectedCountry by remember { mutableStateOf("") }
    var selectedCountryId by remember { mutableStateOf("") }
    var selectedCity by remember { mutableStateOf("") }

    val countries = listOf(
        Country("1", "España"),
        Country("2", "Francia"),
        Country("3", "Italia")
    )

    // Filtrar ciudades según el país seleccionado
    val cities = when (selectedCountryId) {
        "1" -> listOf(City("1", "Madrid"), City("2", "Barcelona"))
        "2" -> listOf(City("3", "París"), City("4", "Lyon"))
        "3" -> listOf(City("5", "Roma"), City("6", "Milán"))
        else -> emptyList()
    }

    Column {
        // Primer dropdown: País
        CustomDropdown(
            value = selectedCountry,
            label = "País",
            placeholder = "Selecciona un país",
            items = countries,
            onItemSelected = { country ->
                selectedCountryId = country.id
                selectedCountry = country.name
                // Limpiar ciudad al cambiar país
                selectedCity = ""
            },
            itemToString = { it.name },
            leadingIcon = Icons.Default.Public,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Segundo dropdown: Ciudad (habilitado solo si hay país seleccionado)
        CustomDropdown(
            value = selectedCity,
            label = "Ciudad",
            placeholder = "Selecciona una ciudad",
            items = cities,
            onItemSelected = { city ->
                selectedCity = city.name
            },
            itemToString = { it.name },
            leadingIcon = Icons.Default.LocationCity,
            enabled = selectedCountryId.isNotBlank(),
            emptyMessage = "Primero selecciona un país",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ============================================
// EJEMPLO 6: Uso con SimpleCustomDropdown
// ============================================

@Composable
fun SimpleDropdownExample() {
    var selectedName by remember { mutableStateOf("") }

    val items = listOf(
        DropdownItem("1", "Primera opción"),
        DropdownItem("2", "Segunda opción"),
        DropdownItem("3", "Tercera opción")
    )

    SimpleCustomDropdown(
        selectedName = selectedName,
        label = "Seleccionar opción",
        placeholder = "Elige...",
        items = items,
        onItemSelected = { item ->
            selectedName = item.name
        },
        leadingIcon = Icons.Default.List,
        modifier = Modifier.fillMaxWidth()
    )
}
*/

