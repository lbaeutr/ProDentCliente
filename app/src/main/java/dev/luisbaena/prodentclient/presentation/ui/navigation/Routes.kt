package dev.luisbaena.prodentclient.presentation.ui.navigation

    /**
     * Objeto que contiene las rutas de navegación de la aplicación
     * Cada constante representa una ruta única para una pantalla o funcionalidad específica
     */

object Routes {

    // Rutas para autenticación
    const val Login = "login"
    const val Register = "register"

    // Rutas principales
    const val Main = "main"
    const val QR = "qr" // Para buscar trabajos


    // Rutas para otras pantallas
    const val MyProfile = "profile"
    const val EditProfile = "edit_profile"
    const val ChangePassword = "change_password"
    const val DeleteAccount = "delete_account"
    const val Clinic = "clinic/{isAdmin}" // Lista de clínicas (con parámetro)
    const val ClinicDetailUser = "clinic_detail_user/{clinicId}" // Detalle USER
    const val ClinicDetailAdmin = "clinic_detail_admin/{clinicId}" // Detalle ADMIN
    const val ClinicUdate = "clinic_update/{clinicId}" // Actualizar clínica
    const val CreateClinic = "create_clinic"
    const val Directory = "directory"
    const val CreateDentis = "create_dentist"
    const val Dentist = "dentist/{isAdmin}" // Lista de dentistas (con parámetro)
    const val DentistDetailUser = "dentist_detail_user/{dentistId}" // Detalle USER
    const val DentistDetailAdmin = "dentist_detail_admin/{dentistId}" // Detalle ADMIN
    const val DentistUpdate = "dentist_update/{dentistId}" // Actualizar dentista
    const val WorkType = "create_worktype"
    const val WorkTypeList = "worktype_list"
    const val WorkTypeDetail = "worktype_detail/{workTypeId}"
    const val WorkTypeUpdate = "worktype_update/{workTypeId}"
    const val Material = "create_material"
    const val MaterialList = "material_list"
    const val MaterialDetail = "material_detail/{materialId}"
    const val MaterialUpdate = "material_update/{materialId}"
    const val CreateWork = "create_work"
    const val WorkList = "worklist"
    const val WorkDetail = "work_detail/{workId}"
    const val WorkUpdate = "work_update/{workId}"
    const val QRBatchGenerator = "qr_batch_generator"

}