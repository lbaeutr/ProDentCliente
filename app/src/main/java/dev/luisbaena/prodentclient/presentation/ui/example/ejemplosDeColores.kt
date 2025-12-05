package dev.luisbaena.prodentclient.presentation.ui.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.luisbaena.prodentclient.ui.theme.*

@Composable
fun ColorBox(color: Color, name: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp, 24.dp)
                .background(color)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(name, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun ColoresProdentDemo() {
    val cs = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        HorizontalDivider(Modifier, DividerDefaults.Thickness, ProdentGray)
        Spacer(Modifier.height(16.dp))
        Text("Colores de Marca", style = MaterialTheme.typography.titleMedium)
        ColorBox(ProdentGreen, "ProdentGreen")
        ColorBox(ProdentGreenDark, "ProdentGreenDark")
        ColorBox(ProdentGreenLight, "ProdentGreenLight")
        ColorBox(ProdentGreenPale, "ProdentGreenPale")
        ColorBox(ProdentGray, "ProdentGray")
        ColorBox(ProdentGrayDark, "ProdentGrayDark")
        ColorBox(ProdentGrayMedium, "ProdentGrayMedium")
        ColorBox(ProdentGrayLight, "ProdentGrayLight")
        ColorBox(ProdentGrayPale, "ProdentGrayPale")
        Spacer(Modifier.height(16.dp))
        HorizontalDivider(Modifier, DividerDefaults.Thickness, ProdentGray)
        Spacer(Modifier.height(16.dp))
        Text("Colores Principales", style = MaterialTheme.typography.titleMedium)
        ColorBox(cs.primary, "primary")
        ColorBox(cs.onPrimary, "onPrimary")
        ColorBox(cs.primaryContainer, "primaryContainer")
        ColorBox(cs.onPrimaryContainer, "onPrimaryContainer")
        Spacer(Modifier.height(16.dp))
        HorizontalDivider(Modifier, DividerDefaults.Thickness, ProdentGray)
        Spacer(Modifier.height(16.dp))
        Text("Secundarios y Terciarios", style = MaterialTheme.typography.titleMedium)
        ColorBox(cs.secondary, "secondary")
        ColorBox(cs.onSecondary, "onSecondary")
        ColorBox(cs.secondaryContainer, "secondaryContainer")
        ColorBox(cs.onSecondaryContainer, "onSecondaryContainer")
        ColorBox(cs.tertiary, "tertiary")
        ColorBox(cs.onTertiary, "onTertiary")
        ColorBox(cs.tertiaryContainer, "tertiaryContainer")
        ColorBox(cs.onTertiaryContainer, "onTertiaryContainer")
        Spacer(Modifier.height(16.dp))
        HorizontalDivider(Modifier, DividerDefaults.Thickness, ProdentGray)
        Spacer(Modifier.height(16.dp))
        Text("Estados y Negocio", style = MaterialTheme.typography.titleMedium)
        ColorBox(Success, "Success")
        ColorBox(Warning, "Warning")
        ColorBox(Info, "Info")
        ColorBox(DentistaColor, "DentistaColor")
        ColorBox(TrabajoColor, "TrabajoColor")
        ColorBox(ClinicaColor, "ClinicaColor")
        ColorBox(PacienteColor, "PacienteColor")
        Spacer(Modifier.height(16.dp))
        HorizontalDivider(Modifier, DividerDefaults.Thickness, ProdentGray)
        Spacer(Modifier.height(16.dp))
        Text("Superficies y Outline", style = MaterialTheme.typography.titleMedium)
        ColorBox(cs.background, "background")
        ColorBox(cs.onBackground, "onBackground")
        ColorBox(cs.surface, "surface")
        ColorBox(cs.onSurface, "onSurface")
        ColorBox(cs.surfaceVariant, "surfaceVariant")
        ColorBox(cs.onSurfaceVariant, "onSurfaceVariant")
        ColorBox(cs.outline, "outline")
    }
}

@Preview(showBackground = true, name = "Colores ProDent", heightDp = 1400)
@Composable
fun PreviewColoresProdentDemo() {
    ProdentclientTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ColoresProdentDemo()
        }
    }
}
