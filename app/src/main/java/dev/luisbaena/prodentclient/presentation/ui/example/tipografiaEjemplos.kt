package dev.luisbaena.prodentclient.presentation.ui.example

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.luisbaena.prodentclient.ui.theme.ProdentclientTheme

@Composable
fun TypographyExamples() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Display Styles
        Text(
            text = "Display Large - Lato Black 57sp",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Text(
            text = "Display Medium - Lato Black 45sp",
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Text(
            text = "Display Small - Lato Black 36sp",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // Headline Styles
        Text(
            text = "Headline Large - Lato Bold 32sp",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Text(
            text = "Headline Medium - Lato Bold 28sp",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Text(
            text = "Headline Small - Lato Bold 24sp",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // Title Styles
        Text(
            text = "Title Large - Lato Regular 22sp",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Text(
            text = "Title Medium - Lato Regular 16sp",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Text(
            text = "Title Small - Lato Regular 14sp",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // Body Styles
        Text(
            text = "Body Large - Lato Regular 16sp con line height 24sp y letter spacing 0.5sp. Perfecto para párrafos largos y contenido principal.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Text(
            text = "Body Medium - Lato Regular 14sp con line height 20sp y letter spacing 0.25sp. Ideal para texto secundario y descripciones.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Text(
            text = "Body Small - Lato Regular 12sp con line height 16sp y letter spacing 0.4sp. Para notas y texto auxiliar.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // Label Styles
        Text(
            text = "Label Large - Lato Regular 14sp",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Text(
            text = "Label Medium - Lato Regular 12sp",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Text(
            text = "Label Small - Lato Regular 11sp",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}

@Preview(showBackground = true,heightDp = 1000)
@Composable
fun TypographyExamplesPreview() {
    ProdentclientTheme {
        TypographyExamples()
    }
}
