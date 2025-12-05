package dev.luisbaena.prodentclient.presentation.ui.navigation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.luisbaena.prodentclient.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * TESTS DE NAVEGACIÓN - FLUJOS COMPLETOS SIMPLIFICADOS
 *
 * Estos tests verifican la navegación de la UI lanzando la MainActivity.
 * Solo verifican que los elementos existan y sean clickeables.
 *
 * IMPORTANTE: Para que estos tests funcionen:
 * 1. Debe haber un usuario autenticado previamente
 * 2. Conecta un dispositivo/emulador antes de ejecutar
 *
 * Para ejecutar:
 * Click derecho en el archivo -> Run 'NavigationFlowTest'
 */
@RunWith(AndroidJUnit4::class)
class NavigationFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    /**
     * TEST 1: Verificar que Bottom Navigation funciona
     * Home -> QR -> Home
     */
    @Test
    fun test_bottom_navigation_home_qr_home() {
        composeTestRule.waitForIdle()

        // Verificar que existe el tab QR
        composeTestRule
            .onNodeWithText("QR")
            .assertExists()
            .assertHasClickAction()

        // Click en QR
        composeTestRule
            .onNodeWithText("QR")
            .performClick()

        composeTestRule.waitForIdle()

        // Verificar que existe el tab Inicio
        composeTestRule
            .onNodeWithText("Inicio")
            .assertExists()
            .assertHasClickAction()

        // Regresar a Inicio
        composeTestRule
            .onNodeWithText("Inicio")
            .performClick()

        composeTestRule.waitForIdle()
    }

    /**
     * TEST 2: Verificar que Drawer se puede abrir
     */
    @Test
    fun test_drawer_se_puede_abrir() {
        composeTestRule.waitForIdle()

        // Intentar abrir drawer
        composeTestRule
            .onAllNodesWithContentDescription("Menú")
            .onFirst()
            .assertExists()
            .performClick()

        composeTestRule.waitForIdle()
    }

    /**
     * TEST 3: Verificar navegación a perfil desde drawer
     */
    @Test
    fun test_navegar_a_perfil_desde_drawer() {
        composeTestRule.waitForIdle()

        // Abrir drawer
        composeTestRule
            .onAllNodesWithContentDescription("Menú")
            .onFirst()
            .performClick()

        composeTestRule.waitForIdle()

        // Click en Mi Perfil
        composeTestRule
            .onNodeWithText("Mi Perfil")
            .assertExists()
            .performClick()

        composeTestRule.waitForIdle()
    }

    /**
     * TEST 4: Cambiar entre tabs múltiples veces
     */
    @Test
    fun test_cambiar_tabs_multiples_veces() {
        composeTestRule.waitForIdle()

        // Ciclo 1: Home -> QR -> Home
        composeTestRule.onNodeWithText("QR").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Inicio").performClick()
        composeTestRule.waitForIdle()

        // Ciclo 2: Home -> QR -> Home
        composeTestRule.onNodeWithText("QR").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Inicio").performClick()
        composeTestRule.waitForIdle()
    }

    /**
     * TEST 5: Verificar que bottom nav es visible
     */
    @Test
    fun test_bottom_navigation_visible() {
        composeTestRule.waitForIdle()

        // Verificar tabs
        composeTestRule
            .onNodeWithText("Inicio")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("QR")
            .assertIsDisplayed()
    }

    /**
     * TEST 6: Navegación completa drawer
     * Abrir drawer -> Seleccionar opción -> Cerrar
     */
    @Test
    fun test_ciclo_completo_drawer() {
        composeTestRule.waitForIdle()

        // Abrir drawer
        composeTestRule
            .onAllNodesWithContentDescription("Menú")
            .onFirst()
            .performClick()

        composeTestRule.waitForIdle()

        // Verificar que drawer tiene opciones
        // (al menos Mi Perfil debería estar visible)
        composeTestRule
            .onNodeWithText("Mi Perfil")
            .assertExists()

        // Navegar a perfil (esto debería cerrar el drawer)
        composeTestRule
            .onNodeWithText("Mi Perfil")
            .performClick()

        composeTestRule.waitForIdle()
    }
}
