package dev.luisbaena.prodentclient.presentation.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.ControlPoint
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.AssignmentInd
import androidx.compose.material.icons.outlined.ControlPoint
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Work
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Items del Navigation Drawer
 */
sealed class DrawerNavItem(
    val route: String,
    val title: String,
    val subtitle: String? = null,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val allowedRoles: List<UserRole>,
    val section: DrawerSection,
    val adminSubSection: AdminSubSection? = null // Para sub-agrupar en ADMINISTRACIÓN
) {

    object MyProfile : DrawerNavItem(
        route = Routes.MyProfile,
        title = "Mi Perfil",
        subtitle = "Información de personal",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        allowedRoles = listOf(UserRole.USER, UserRole.ADMIN),
        section = DrawerSection.MY_ACCOUNT
    )

    object ClinicUser : DrawerNavItem(
        route = "clinic/false", // isAdmin = false
        title = "Clinicas",
        subtitle = "Información de clínicas",
        selectedIcon = Icons.Filled.Apartment,
        unselectedIcon = Icons.Outlined.Apartment,
        allowedRoles = listOf(UserRole.USER, UserRole.ADMIN),
        section = DrawerSection.INFORMATION
    )

    object CreateClinic : DrawerNavItem(
        route = Routes.CreateClinic,
        title = "Crear Clinica",
        subtitle = "Crear nueva clínica dental",
        selectedIcon = Icons.Filled.ControlPoint,
        unselectedIcon = Icons.Outlined.ControlPoint,
        allowedRoles = listOf(UserRole.ADMIN),
        section = DrawerSection.ADMINISTRATION,
        adminSubSection = AdminSubSection.CLINICS
    )

    object ClinicAdmin : DrawerNavItem(
        route = "clinic/true", // isAdmin = true
        title = "Administrar Clínicas",
        subtitle = "Información y gestión de clínicas",
        selectedIcon = Icons.Filled.Apartment,
        unselectedIcon = Icons.Outlined.Apartment,
        allowedRoles = listOf(UserRole.ADMIN),
        section = DrawerSection.ADMINISTRATION,
        adminSubSection = AdminSubSection.CLINICS
    )

    object RegisterAccount : DrawerNavItem(
        route = Routes.Register,
        title = "Registrar Cuenta",
        subtitle = "Registrar nueva cuenta de usuario",
        selectedIcon = Icons.Filled.Add,
        unselectedIcon = Icons.Outlined.Add,
        allowedRoles = listOf(UserRole.ADMIN),
        section = DrawerSection.ADMINISTRATION,
        adminSubSection = AdminSubSection.ACCOUNTS
    )

    object DeleteAccount : DrawerNavItem(
        route = Routes.DeleteAccount,
        title = "Eliminar Cuenta",
        subtitle = "Eliminar cuenta permanentemente",
        selectedIcon = Icons.Filled.Delete,
        unselectedIcon = Icons.Outlined.Delete,
        allowedRoles = listOf(UserRole.ADMIN),
        section = DrawerSection.ADMINISTRATION,
        adminSubSection = AdminSubSection.ACCOUNTS
    )

    object Directory : DrawerNavItem(
        route = Routes.Directory,
        title = "Directorio",
        subtitle = "Información de usuarios",
        selectedIcon = Icons.Filled.AssignmentInd,
        unselectedIcon = Icons.Outlined.AssignmentInd,
        allowedRoles = listOf(UserRole.USER, UserRole.ADMIN),
        section = DrawerSection.INFORMATION
    )

    object CreateDentis : DrawerNavItem(
        route = Routes.CreateDentis,
        title = "Crear Dentista",
        subtitle = "Crear nuevo dentista",
        selectedIcon = Icons.Filled.Add,
        unselectedIcon = Icons.Outlined.Add,
        allowedRoles = listOf(UserRole.ADMIN),
        section = DrawerSection.ADMINISTRATION,
        adminSubSection = AdminSubSection.DENTISTS
    )

    object DentistUser : DrawerNavItem(
        route = "dentist/false", // isAdmin = false
        title = "Dentistas",
        subtitle = "Información de dentistas",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        allowedRoles = listOf(UserRole.USER, UserRole.ADMIN),
        section = DrawerSection.INFORMATION
    )

    object DentistAdmin : DrawerNavItem(
        route = "dentist/true", // isAdmin = true
        title = "Administrar Dentistas",
        subtitle = "Información y gestión de dentistas",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        allowedRoles = listOf(UserRole.ADMIN),
        section = DrawerSection.ADMINISTRATION,
        adminSubSection = AdminSubSection.DENTISTS
    )

    object WorkType : DrawerNavItem(
        route = Routes.WorkType,
        title = "Crear Tipo de Trabajo",
        subtitle = "Crear nuevo tipo de trabajo",
        selectedIcon = Icons.Filled.Add,
        unselectedIcon = Icons.Outlined.Add,
        allowedRoles = listOf(UserRole.ADMIN),
        section = DrawerSection.ADMINISTRATION,
        adminSubSection = AdminSubSection.WORK_TYPES
    )

    object WorkTypeList : DrawerNavItem(
        route = Routes.WorkTypeList,
        title = "Tipos de Trabajo",
        subtitle = "Lista de tipos de trabajo",
        selectedIcon = Icons.AutoMirrored.Filled.FormatListBulleted,
        unselectedIcon = Icons.AutoMirrored.Outlined.FormatListBulleted,
        allowedRoles = listOf(UserRole.ADMIN),
        section = DrawerSection.ADMINISTRATION,
        adminSubSection = AdminSubSection.WORK_TYPES
    )

    object Material : DrawerNavItem(
        route = Routes.Material,
        title = "Crear Material",
        subtitle = "Crear nuevo material",
        selectedIcon = Icons.Filled.Add,
        unselectedIcon = Icons.Outlined.Add,
        allowedRoles = listOf(UserRole.ADMIN),
        section = DrawerSection.ADMINISTRATION,
        adminSubSection = AdminSubSection.MATERIALS
    )

    object MaterialList : DrawerNavItem(
        route = Routes.MaterialList,
        title = "Materiales",
        subtitle = "Lista de materiales",
        selectedIcon = Icons.Filled.FormatListNumbered,
        unselectedIcon = Icons.Outlined.FormatListNumbered,
        allowedRoles = listOf(UserRole.ADMIN),
        section = DrawerSection.ADMINISTRATION,
        adminSubSection = AdminSubSection.MATERIALS
    )

    object CreateWork : DrawerNavItem(
        route = Routes.CreateWork,
        title = "Crear Trabajo",
        subtitle = "Dar de alta nuevo trabajo",
        selectedIcon = Icons.Filled.Add,
        unselectedIcon = Icons.Outlined.Add,
        allowedRoles = listOf(UserRole.USER),
        section = DrawerSection.TRABAJOS
    )

    object WorkList : DrawerNavItem(
        route = Routes.WorkList,
        title = "Trabajos",
        subtitle = "Listar trabajos",
        selectedIcon = Icons.Filled.Work,
        unselectedIcon = Icons.Outlined.Work,
        allowedRoles = listOf(UserRole.USER),
        section = DrawerSection.TRABAJOS
    )

    object QRBatchGenerator : DrawerNavItem(
        route = Routes.QRBatchGenerator,
        title = "Generar Códigos QR",
        subtitle = "Generar QR en lote",
        selectedIcon = Icons.Filled.QrCode2,
        unselectedIcon = Icons.Outlined.QrCode2,
        allowedRoles = listOf(UserRole.USER),
        section = DrawerSection.UTILS
    )
}

    /**
     * Gestión de visibilidad según rol
     * Aquí se define qué items son visibles para cada rol de usuario.
     */
enum class UserRole {
    USER,
    ADMIN
}

// Función para filtrar items según el rol
fun getDrawerItemsForRole(userRole: UserRole): List<DrawerNavItem> {
    return when (userRole) {
        UserRole.ADMIN -> drawerNavItems // ADMIN ve TODOS los items
        UserRole.USER -> drawerNavItems.filter { it.allowedRoles.contains(UserRole.USER) } // USER solo ve los permitidos
    }
}

// Lista de todos los items del drawer
val drawerNavItems = listOf(
    DrawerNavItem.WorkList,
    DrawerNavItem.CreateWork,
    DrawerNavItem.ClinicUser,
    DrawerNavItem.DentistUser,
    DrawerNavItem.Directory,
    DrawerNavItem.MyProfile,
    DrawerNavItem.QRBatchGenerator,
    DrawerNavItem.RegisterAccount,
    DrawerNavItem.DeleteAccount,
    DrawerNavItem.CreateClinic,
    DrawerNavItem.ClinicAdmin,
    DrawerNavItem.CreateDentis,
    DrawerNavItem.DentistAdmin,
    DrawerNavItem.WorkType,
    DrawerNavItem.WorkTypeList,
    DrawerNavItem.Material,
    DrawerNavItem.MaterialList,
)

// LAS DOS SIGUIENTES ADICIONES SON NUEVAS Y SIRVEN PARA AGRUPAR POR SECCIÓN

// Enum para las secciones
enum class DrawerSection(val title: String) {
    MY_ACCOUNT("MI CUENTA"),
    TRABAJOS("TRABAJOS"),
    ADMINISTRATION("ADMINISTRACIÓN"),
    INFORMATION("INFORMACIÓN"),
    UTILS("UTILIDADES")
}

// Enum para las sub-secciones de ADMINISTRACIÓN
enum class AdminSubSection(val title: String) {
    ACCOUNTS("Gestión de Cuentas"),
    CLINICS("Gestión de Clínicas"),
    DENTISTS("Gestión de Dentistas"),
    WORK_TYPES("Gestión de Tipos de Trabajo"),
    MATERIALS("Gestión de Materiales")
}

// Función para agrupar por sección
fun getDrawerItemsBySection(userRole: UserRole): Map<DrawerSection, List<DrawerNavItem>> {
    return getDrawerItemsForRole(userRole)
        .groupBy { it.section }
}

// Función para agrupar items de ADMINISTRACIÓN por sub-sección
fun getAdminItemsBySubSection(adminItems: List<DrawerNavItem>): Map<AdminSubSection, List<DrawerNavItem>> {
    return adminItems
        .filter { it.adminSubSection != null }
        .groupBy { it.adminSubSection!! }
}

