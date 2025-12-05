package dev.luisbaena.prodentclient.ui.theme

import androidx.compose.ui.graphics.Color

// =====================================================
//  COLORES DE MARCA - Basados en el logo ProDent
// =====================================================

// 🟢 VERDE PRODENT (Color principal del logo)
val ProdentGreen = Color(0xFF26D2A0)        // Verde turquesa principal
val ProdentGreenDark = Color(0xFF1DB88A)    // Verde oscuro
val ProdentGreenLight = Color(0xFF5FDDBA)   // Verde claro
val ProdentGreenPale = Color(0xFFE5F9F3)    // Verde muy claro (fondos)
val ProdentGreenDarkContainer = Color(0xFF1A3D35) // Verde muy oscuro para contenedores en dark mode

//  GRISES PRODENT (Del texto del logo)
val ProdentGray = Color(0xFF3D4451)         // Gris oscuro del logo
val ProdentGrayDark = Color(0xFF2A2E39)     // Gris más oscuro
val ProdentGrayMedium = Color(0xFF6B7280)   // Gris medio
val ProdentGrayLight = Color(0xFFE5E7EB)    // Gris claro
val ProdentGrayPale = Color(0xFFF9FAFB)     // Gris muy claro

// =====================================================
//  COLORES PRIMARIOS
// =====================================================
val Primary = ProdentGreen
val PrimaryDark = ProdentGreenDark
val PrimaryLight = ProdentGreenLight

// Light Mode
val OnPrimary = Color(0xFFFFFFFF)           // Blanco sobre verde
val PrimaryContainer = ProdentGreenPale
val OnPrimaryContainer = ProdentGreenDark

// Dark Mode
val OnPrimaryDark = Color(0xFFFFFFFF)       // Blanco sobre verde en dark mode
val PrimaryContainerDark = ProdentGreenDarkContainer
val OnPrimaryContainerDark = ProdentGreenLight

// =====================================================
//  COLORES SECUNDARIOS - Complementarios
// =====================================================
val Secondary = Color(0xFF8B5CF6)           // Morado (complementario)
val SecondaryDark = Color(0xFF7C3AED)
val SecondaryLight = Color(0xFFA78BFA)

val OnSecondary = Color(0xFFFFFFFF)
val SecondaryContainer = Color(0xFFF3E8FF)
val OnSecondaryContainer = Color(0xFF5B21B6)

// =====================================================
// COLORES TERCIARIOS - Accentos cálidos
// =====================================================
val Tertiary = Color(0xFFFF6B6B)            // Coral/Naranja suave
val TertiaryDark = Color(0xFFEE5A52)
val TertiaryLight = Color(0xFFFF8787)

val OnTertiary = Color(0xFFFFFFFF)
val TertiaryContainer = Color(0xFFFFE5E5)
val OnTertiaryContainer = Color(0xFFCC0000)

// =====================================================
// ️ COLORES DE ERROR
// =====================================================
val Error = Color(0xFFDC2626)               // Rojo error
val ErrorDark = Color(0xFFB91C1C)
val ErrorLight = Color(0xFFEF4444)

val OnError = Color(0xFFFFFFFF)
val ErrorContainer = Color(0xFFFEE2E2)
val OnErrorContainer = Color(0xFF991B1B)

// =====================================================
//  COLORES DE ÉXITO
// =====================================================
val Success = ProdentGreen                  // Usa el verde de marca
val SuccessDark = ProdentGreenDark
val SuccessLight = ProdentGreenLight
val SuccessContainer = ProdentGreenPale

//  COLORES DE ADVERTENCIA
val Warning = Color(0xFFF59E0B)             // Amarillo/Naranja
val WarningDark = Color(0xFFD97706)
val WarningLight = Color(0xFFFBBF24)
val WarningContainer = Color(0xFFFEF3C7)

// ℹ COLORES DE INFO
val Info = Color(0xFF3B82F6)                // Azul
val InfoDark = Color(0xFF2563EB)
val InfoLight = Color(0xFF60A5FA)
val InfoContainer = Color(0xFFDCEEFE)

// =====================================================
//  COLORES DE SUPERFICIE (Fondos)
// =====================================================

//  LIGHT MODE
val BackgroundLight = Color(0xFFFFFFFF)     // Blanco puro
val SurfaceLight = Color(0xFFFAFAFA)        // Gris muy claro
val SurfaceVariantLight = ProdentGrayPale   // Gris suave
val OnSurfaceLight = ProdentGray            // Texto principal
val OnSurfaceVariantLight = ProdentGrayMedium // Texto secundario

//  DARK MODE
val BackgroundDark = Color(0xFF2D2D2D)       // Negro profundo
val SurfaceDark = Color(0xFF242424)           // Gris muy oscuro
val SurfaceVariantDark = Color(0xFF2D2D2D)  // Gris oscuro
val OnSurfaceDark = Color(0xFFE5E7EB)       // Texto principal claro
val OnSurfaceVariantDark = Color(0xFF9CA3AF) // Texto secundario

// =====================================================
//  COLORES ESPECÍFICOS DE NEGOCIO
// =====================================================
val DentistaColor = Color(0xFF06B6D4)       // Cyan
val TrabajoColor = Color(0xFFEF4444)        // Rojo
val ClinicaColor = Color(0xFF8B5CF6)        // Morado
val PacienteColor = ProdentGreen            // Verde de marca

//  ESTADOS DE TRABAJO
val StatusPendiente = Color(0xFFF59E0B)     // Amarillo
val StatusEnProceso = Color(0xFF3B82F6)     // Azul
val StatusCompletado = ProdentGreen         // Verde de marca
val StatusCancelado = Color(0xFFEF4444)     // Rojo

// =====================================================
//  COLORES OUTLINE Y DIVISORES
// =====================================================
val OutlineLight = ProdentGrayLight
val OutlineDark = Color(0xFF404040)
