// MainActivity.kt - COMPLETA
package dev.luisbaena.prodentclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.luisbaena.prodentclient.presentation.ui.navigation.AppNavigation
import dev.luisbaena.prodentclient.presentation.ui.screens.auth.LoginScreen
import dev.luisbaena.prodentclient.presentation.viewmodel.AuthViewModel
import dev.luisbaena.prodentclient.ui.theme.ProdentclientTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            ProdentclientTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
//
//// 🚀 FUNCIÓN PRINCIPAL DE TU APP
//@Composable
//fun ProdentApp() {
//    val navController = rememberNavController()  // Controlador de navegación
//    val authViewModel: AuthViewModel = hiltViewModel()  // ViewModel global de auth
//    val uiState by authViewModel.uiState.collectAsState()  // Estado del usuario
//
//    // 🎯 LÓGICA: ¿Dónde empezar?
//    val startDestination = if (uiState.user != null) {
//        "main"    // Si hay usuario → Pantalla principal
//    } else {
//        "login"   // Si NO hay usuario → Login
//    }
//
//    // 🧭 NAVEGACIÓN ENTRE PANTALLAS
//    NavHost(
//        navController = navController,
//        startDestination = startDestination  // Pantalla inicial
//    ) {
//        // 🔐 PANTALLA DE LOGIN
//        composable("login") {
//            LoginScreen(
//                onLoginSuccess = {
//                    // ✅ Login exitoso → Ir a main y limpiar historial
//                    navController.navigate("main") {
//                        popUpTo("login") { inclusive = true }  // No puede volver con "back"
//                    }
//                }
//            )
//        }
//
//        // 🏠 PANTALLA PRINCIPAL (temporal)
//        composable("main") {
//            MainScreen(
//                onLogout = {
//                    // 🚪 Logout → Volver a login
//                    authViewModel.logout()
//                    navController.navigate("login") {
//                        popUpTo("main") { inclusive = true }
//                    }
//                }
//            )
//        }
//    }
//}

// 🏠 PANTALLA PRINCIPAL TEMPORAL
//@Composable
//fun MainScreen(
//    onLogout: () -> Unit,
//    authViewModel: AuthViewModel = hiltViewModel()
//) {
//    val uiState by authViewModel.uiState.collectAsState()
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(24.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//
//        // 🎉 BIENVENIDA
//        Text(
//            text = "¡Bienvenido a ProDent!",
//            style = MaterialTheme.typography.headlineMedium.copy(
//                fontWeight = FontWeight.Bold
//            ),
//            color = MaterialTheme.colorScheme.primary
//        )
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        // 👤 INFO DEL USUARIO
//        uiState.user?.let { user ->
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                colors = CardDefaults.cardColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer
//                )
//            ) {
//                Column(
//                    modifier = Modifier.padding(20.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        text = "👤 ${user.nombre} ${user.apellido}",
//                        style = MaterialTheme.typography.titleLarge,
//                        color = MaterialTheme.colorScheme.onPrimaryContainer
//                    )
//                    Text(
//                        text = "📧 ${user.email}",
//                        style = MaterialTheme.typography.bodyLarge,
//                        color = MaterialTheme.colorScheme.onPrimaryContainer,
//                        modifier = Modifier.padding(top = 4.dp)
//                    )
//                    Text(
//                        text = "🏷️ Rol: ${user.role}",
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onPrimaryContainer,
//                        modifier = Modifier.padding(top = 4.dp)
//                    )
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(32.dp))
//
//        // 🚪 BOTÓN DE LOGOUT
//        OutlinedButton(
//            onClick = onLogout,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("🚪 Cerrar Sesión")
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // 💡 NOTA TEMPORAL
//        Text(
//            text = "Esta es una pantalla temporal.\n¡Pronto tendrás gestión de trabajos, dentistas y más!",
//            style = MaterialTheme.typography.bodySmall,
//            color = MaterialTheme.colorScheme.onSurfaceVariant
//        )
//    }
//}