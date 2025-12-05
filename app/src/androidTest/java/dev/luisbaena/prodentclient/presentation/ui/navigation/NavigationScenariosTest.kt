package dev.luisbaena.prodentclient.presentation.ui.navigation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.luisbaena.prodentclient.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * TESTS DE NAVEGACIÓN - ESCENARIOS REALES SIMPLIFICADOS
 *
 * Tests que simulan flujos de usuario reales lanzando la MainActivity.
 * Solo verifican interacciones de UI.
 *
 * IMPORTANTE: Requiere usuario autenticado previamente en la app.
 */
@RunWith(AndroidJUnit4::class)
class NavigationScenariosTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    /**
     * ESCENARIO 1: Usuario navega por el drawer
     * Abrir drawer -> Ver opciones -> Cerrar
     */
    @Test
    fun escenario_explorar_drawer() {
        composeTestRule.waitForIdle()

        // Abrir drawer
        composeTestRule
            .onAllNodesWithContentDescription("Menú")
            .onFirst()
            .performClick()

        composeTestRule.waitForIdle()

        // Verificar que hay opciones en el drawer
        composeTestRule
            .onNodeWithText("Mi Perfil")
            .assertExists()
    }

    /**
     * ESCENARIO 2: Usuario cambia entre tabs varias veces
     * Simula usuario explorando la app
     */
    @Test
    fun escenario_explorar_tabs_bottom_nav() {
        composeTestRule.waitForIdle()

        // Ir a QR
        composeTestRule.onNodeWithText("QR").performClick()
        composeTestRule.waitForIdle()

        // Volver a Inicio
        composeTestRule.onNodeWithText("Inicio").performClick()
        composeTestRule.waitForIdle()

        // Ir a QR nuevamente
        composeTestRule.onNodeWithText("QR").performClick()
        composeTestRule.waitForIdle()

        // Volver a Inicio
        composeTestRule.onNodeWithText("Inicio").performClick()
        composeTestRule.waitForIdle()
    }

    /**
     * ESCENARIO 3: Usuario va a su perfil
     */
    @Test
    fun escenario_ir_a_perfil() {
        composeTestRule.waitForIdle()

        // Abrir drawer
        composeTestRule
            .onAllNodesWithContentDescription("Menú")
            .onFirst()
            .performClick()

        composeTestRule.waitForIdle()

        // Ir a perfil
        composeTestRule
            .onNodeWithText("Mi Perfil")
            .performClick()

        composeTestRule.waitForIdle()
    }

    /**
     * ESCENARIO 4: Verificar elementos de navegación existen
     */
    @Test
    fun escenario_verificar_elementos_navegacion() {
        composeTestRule.waitForIdle()

        // Bottom nav debe ser visible
        composeTestRule
            .onNodeWithText("Inicio")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("QR")
            .assertIsDisplayed()

        // Botón menú debe existir
        composeTestRule
            .onAllNodesWithContentDescription("Menú")
            .onFirst()
            .assertExists()
    }
}

