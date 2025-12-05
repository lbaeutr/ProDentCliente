package dev.luisbaena.prodentclient.presentation.ui.components.common.inputs

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector


    /**
     * Versión simplificada con objetos que tienen id y nombre
     *
     * No usado ya que hemos usado CustomDropdown directamente con data class específicas
     */
data class DropdownItem(
    val id: String,
    val name: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleCustomDropdown(
    selectedName: String,
    label: String,
    placeholder: String = "",
    items: List<DropdownItem>,
    onItemSelected: (DropdownItem) -> Unit,
    isLoading: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    emptyMessage: String = "No hay opciones disponibles",
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null
) {
    CustomDropdown(
        value = selectedName,
        label = label,
        placeholder = placeholder,
        items = items,
        onItemSelected = onItemSelected,
        itemToString = { it.name },
        isLoading = isLoading,
        isError = isError,
        errorMessage = errorMessage,
        emptyMessage = emptyMessage,
        leadingIcon = leadingIcon,
        enabled = enabled,
        modifier = modifier,
        onRetry = onRetry
    )
}

