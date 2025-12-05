package dev.luisbaena.prodentclient.presentation.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.luisbaena.prodentclient.presentation.ui.components.AppNavigationDrawer
import dev.luisbaena.prodentclient.presentation.ui.screens.auth.DeleteAccountScreen
import dev.luisbaena.prodentclient.presentation.ui.screens.auth.LoginScreen
import dev.luisbaena.prodentclient.presentation.ui.screens.auth.RegisterScreen
import dev.luisbaena.prodentclient.presentation.ui.screens.clinic.ClinicScreen
import dev.luisbaena.prodentclient.presentation.ui.screens.clinic.ClinicaDetailScreenAdmin
import dev.luisbaena.prodentclient.presentation.ui.screens.clinic.ClinicaDetailScreenUser
import dev.luisbaena.prodentclient.presentation.ui.screens.clinic.CreateClinicaScreen
import dev.luisbaena.prodentclient.presentation.ui.screens.clinic.EditClinicaScreen
import dev.luisbaena.prodentclient.presentation.ui.screens.dentist.CreateDentistScreen
import dev.luisbaena.prodentclient.presentation.ui.screens.dentist.DentistDetailScreenAdmin
import dev.luisbaena.prodentclient.presentation.ui.screens.dentist.DentistDetailScreenUser
import dev.luisbaena.prodentclient.presentation.ui.screens.dentist.DentistsScreen
import dev.luisbaena.prodentclient.presentation.ui.screens.dentist.EditDentistScreen
import dev.luisbaena.prodentclient.presentation.ui.screens.directory.DirectoryScreen
import dev.luisbaena.prodentclient.presentation.ui.screens.home.HomeScreen
import dev.luisbaena.prodentclient.presentation.ui.screens.material.CreateMaterialScreen
import dev.luisbaena.prodentclient.presentation.ui.screens.material.EditMaterialScreen
import dev.luisbaena.prodentclient.presentation.ui.screens.material.MaterialDetailScreen
import dev.luisbaena.prodentclient.presentation.ui.screens.material.MaterialsScreen
import dev.luisbaena.prodentclient.presentation.ui.screens.profile.ChangePasswordScreen
import dev.luisbaena.prodentclient.presentation.ui.screens.profile.EditProfileScreen
import dev.luisbaena.prodentclient.presentation.ui.screens.profile.MyProfileScreen
import dev.luisbaena.prodentclient.presentation.ui.screens.search.SearchScreen
import dev.luisbaena.prodentclient.presentation.ui.screens.work.CreateWorkScreen
import dev.luisbaena.prodentclient.presentation.ui.screens.qr.QrGeneratorScreen
import dev.luisbaena.prodentclient.presentation.ui.screens.work.WorkDetailScreen
import dev.luisbaena.prodentclient.presentation.ui.screens.work.WorkEditScreen
import dev.luisbaena.prodentclient.presentation.ui.screens.work.WorksScreen
import dev.luisbaena.prodentclient.presentation.ui.screens.workType.CreateWorkTypeScreen
import dev.luisbaena.prodentclient.presentation.ui.screens.workType.EditWorkTypeScreen
import dev.luisbaena.prodentclient.presentation.ui.screens.workType.WorkTypeDetailScreen
import dev.luisbaena.prodentclient.presentation.ui.screens.workType.WorkTypesScreen
import dev.luisbaena.prodentclient.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

    /**
     * Sistema de Navegación de la App
     * Aquí se define la lógica de navegación entre pantallas
     * y se inicializan las rutas principales.
     */

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()  // Controlador de navegación
    val authViewModel: AuthViewModel = hiltViewModel()  // ViewModel global de auth
    val uiState by authViewModel.uiState.collectAsState()  // Estado del usuario

    val animationDuration = 500

    // Estado del drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed) // Estado inicial cerrado
    val scope = rememberCoroutineScope() // Alcance para lanzar corutinas

    // Función para abrir el drawer
    val openDrawer: () -> Unit = {
        scope.launch {
            drawerState.open()
        }
    }

    // Observar la ruta actual para gestionar el drawer
    val navBackStackEntry by navController.currentBackStackEntryAsState() // Entrada actual en la pila de navegación
    val currentRoute = navBackStackEntry?.destination?.route

    // Rutas que NO tienen drawer ni bottom bar
    val routesWithoutNavigation = listOf(Routes.Login)
    val showNavigation = currentRoute !in routesWithoutNavigation

    // Navegar automáticamente a Main si el usuario ya está autenticado
    LaunchedEffect(uiState.user) {
        if (uiState.user != null && currentRoute == Routes.Login) {
            navController.navigate(Routes.Main) {
                popUpTo(Routes.Login) { inclusive = true }
            }
        }
    }

        // Estructura del Drawer y NavHost
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppNavigationDrawer(
                navController = navController,
                authViewModel = authViewModel,
                onCloseDrawer = {
                    scope.launch {
                        drawerState.close()
                    }
                },
                isDrawerOpen = drawerState.isOpen
            )
        },
        gesturesEnabled = showNavigation  //  activo en pantallas autenticadas
    ) {
        NavHost(
            navController = navController,
            startDestination = Routes.Login, // Siempre inicia en Login
            enterTransition = {
                fadeIn(animationSpec = tween(animationDuration))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(animationDuration))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(animationDuration))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(animationDuration))
            }
        ) {
            // Pantalla de Login
            composable(Routes.Login) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Routes.Main) {
                            popUpTo(Routes.Login) { inclusive = true }
                        }
                    }
                )
            }

            // Pantalla de Registro
            composable(Routes.Register) {
                RegisterScreen(
                    navController = navController,
                    onRegisterSuccess = {
                        navController.navigate(Routes.Main)
                    },
                    onOpenDrawer = openDrawer
                )
            }

            // Pantalla de Perfil
            composable(Routes.MyProfile) {
                MyProfileScreen(
                    navController = navController,
                    onLogout = {
                        authViewModel.logout {
                            navController.navigate(Routes.Login) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    onOpenDrawer = openDrawer
                )
            }

            // Editar Perfil
            composable(Routes.EditProfile) {
                EditProfileScreen(
                    navController = navController,
                    onOpenDrawer = openDrawer
                )
            }

            // Cambiar Contraseña
            composable(Routes.ChangePassword) {
                ChangePasswordScreen(
                    navController = navController,
                    onOpenDrawer = openDrawer
                )
            }
            // Eliminar Cuenta (Admin)
            composable(Routes.DeleteAccount) {
                DeleteAccountScreen(
                    navController = navController,
                    onOpenDrawer = openDrawer
                )
            }

            // Pantalla Principal
            composable(Routes.Main) {
                HomeScreen(
                    navController = navController,
                    onOpenDrawer = openDrawer
                )
            }

            // Pantalla de Clínica (INFORMACIÓN y ADMINISTRACIÓN unificadas)
            composable(Routes.Clinic) { backStackEntry ->
                val isAdmin = backStackEntry.arguments?.getString("isAdmin")?.toBoolean() ?: false
                ClinicScreen(
                    navController = navController,
                    onOpenDrawer = openDrawer,
                    isAdminMode = isAdmin
                )
            }

            // Pantalla de Búsqueda
            composable(Routes.QR) {
                SearchScreen(
                    navController = navController,
                    onOpenDrawer = openDrawer
                )
            }
            // Pantalla de Directorio
            composable(Routes.Directory) {
                DirectoryScreen(
                    navController = navController,
                    onOpenDrawer = openDrawer
                )
            }

            // Pantalla de Crear Clínica
            composable(Routes.CreateClinic) {
                CreateClinicaScreen(
                    navController = navController,
                    onOpenDrawer = openDrawer
                )
            }

            // Pantalla de Detalle de Clínica - USUARIO
            composable(Routes.ClinicDetailUser) { backStackEntry ->
                val clinicaId = backStackEntry.arguments?.getString("clinicId") ?: ""
                ClinicaDetailScreenUser(
                    clinicaId = clinicaId,
                    navController = navController,
                    onOpenDrawer = openDrawer
                )
            }

            // Pantalla de Detalle de Clínica - ADMIN
            composable(Routes.ClinicDetailAdmin) { backStackEntry ->
                val clinicaId = backStackEntry.arguments?.getString("clinicId") ?: ""
                ClinicaDetailScreenAdmin(
                    clinicaId = clinicaId,
                    navController = navController,
                    onOpenDrawer = openDrawer
                )
            }

            // Pantalla de Actualizar Clínica
            composable(Routes.ClinicUdate) { backStackEntry ->
                EditClinicaScreen(
                    navController = navController,
                    onOpenDrawer = openDrawer,
                    clinicaId = backStackEntry.arguments?.getString("clinicId") ?: ""
                )
            }

            // Pantalla de Crear Dentista
            composable(Routes.CreateDentis) { backStackEntry ->
                CreateDentistScreen(
                    navController = navController,
                    onOpenDrawer = openDrawer
                )
            }

            // Pantalla de Dentistas (INFORMACIÓN y ADMINISTRACIÓN unificadas)
            composable(Routes.Dentist) { backStackEntry ->
                val isAdmin = backStackEntry.arguments?.getString("isAdmin")?.toBoolean() ?: false
                DentistsScreen(
                    navController = navController,
                    onOpenDrawer = openDrawer,
                    isAdminMode = isAdmin
                )
            }

            // Pantalla de Detalle de Dentista
            composable(Routes.DentistDetailUser) { backStackEntry ->
                val dentistId = backStackEntry.arguments?.getString("dentistId") ?: ""
                DentistDetailScreenUser(
                    dentistId = dentistId,
                    navController = navController,
                    onOpenDrawer = openDrawer
                )
            }
            composable(Routes.DentistDetailAdmin) { backStackEntry ->
                val dentistId = backStackEntry.arguments?.getString("dentistId") ?: ""
                DentistDetailScreenAdmin (
                    dentistId = dentistId,
                    navController = navController,
                    onOpenDrawer = openDrawer
                )
            }
            composable(Routes.DentistUpdate){
                val dentistId = it.arguments?.getString("dentistId") ?: ""
                EditDentistScreen (
                    navController = navController,
                    onOpenDrawer = openDrawer,
                    dentistId = dentistId
                )
            }
            composable(Routes.WorkType){
                CreateWorkTypeScreen(
                    navController = navController,
                    onOpenDrawer = openDrawer
                )
            }
            composable(Routes.WorkTypeList){
                WorkTypesScreen(
                    navController = navController,
                    onOpenDrawer = openDrawer
                )
            }
            composable(Routes.WorkTypeDetail){
                val workTypeId = it.arguments?.getString("workTypeId") ?: ""
                WorkTypeDetailScreen(
                    workTypeId = workTypeId,
                    navController = navController,
                    onOpenDrawer = openDrawer
                )
            }
            composable(Routes.WorkTypeUpdate) {
                val workTypeId = it.arguments?.getString("workTypeId") ?: ""
                EditWorkTypeScreen (
                    navController = navController,
                    onOpenDrawer = openDrawer,
                    workTypeId = workTypeId
                )
            }
            composable(Routes.Material){
                CreateMaterialScreen(
                    navController = navController,
                    onOpenDrawer = openDrawer
                )
            }
            composable(Routes.MaterialList){
                MaterialsScreen(
                    navController = navController,
                    onOpenDrawer = openDrawer
                )
            }
            composable(Routes.MaterialDetail){
                val material_detail = it.arguments?.getString("materialId") ?: ""
                MaterialDetailScreen (
                    navController = navController,
                    onOpenDrawer = openDrawer,
                    materialId = material_detail
                )
            }
            composable(Routes.MaterialUpdate){
                val materialId = it.arguments?.getString("materialId") ?: ""
                EditMaterialScreen (
                    navController = navController,
                    onOpenDrawer = openDrawer,
                    materialId = materialId
                )
            }

            composable(Routes.CreateWork){
                CreateWorkScreen(
                    navController = navController,
                    onOpenDrawer = openDrawer
                )
            }

            composable(Routes.QRBatchGenerator){
                QrGeneratorScreen(
                    navController = navController,
                    onOpenDrawer = openDrawer
                )
            }

            composable(Routes.WorkList){
                WorksScreen(
                    navController = navController,
                    onOpenDrawer = openDrawer
                )
            }

            composable(Routes.WorkDetail) { backStackEntry ->
                val workId = backStackEntry.arguments?.getString("workId") ?: ""
                WorkDetailScreen(
                    workId = workId,
                    navController = navController,
                    onOpenDrawer = openDrawer
                )
            }

            composable(Routes.WorkUpdate) { backStackEntry ->
                val workId = backStackEntry.arguments?.getString("workId") ?: ""
                WorkEditScreen(
                    workId = workId,
                    navController = navController,
                    onOpenDrawer = openDrawer
                )
            }
        }
    }
}