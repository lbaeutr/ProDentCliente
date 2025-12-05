package dev.luisbaena.prodentclient.domain.model

    /**
     * Work status enum
     * Estados posibles de un trabajo en el laboratorio
     */
enum class WorkStatus(val value: String, val displayName: String) {
    // Estado inicial
    PLASTER("ESCAYOLA", "Escayola"),

    // Estados de Metal
    METAL_LAB("METAL_LAB", "Metal Lab"),
    METAL_CAD("METAL_CAD", "Metal CAD"),
    METAL_TICARE("METAL_TICARE", "Metal Ticare"),
    METAL_CNC("METAL_CNC", "Metal CNC"),
    METAL_IDEA("METAL_IDEA", "Metal Idea"),

    // Estados de Resina
    RESIN_LAB("RESINA_LAB", "Resina Lab"),
    RESIN_SH("RESINA_SH", "Resina SH"),
    RESIN_ORTOHOMAX("RESINA_ORTOHOMAX", "Resina Ortohomax"),

    // Estados de Clínica
    CLINIC_CLIENT("CLINICA_METAL", "Clínica Metal"),
    CLINIC_RODETES("CLINICA_RODETES", "Clínica Rodetes"),
    CLINIC_BIZCOCHO("CLINICA_BIZCOCHO", "Clínica Bizcocho"),
    CLINIC_AESTHETIC("CLINICA_ESTETICA", "Clínica Estética"),
    CERAMIC("CERAMICA", "Cerámica"),

    // Estados finales
    CANCELLED("CANCELADO", "Cancelado"),
    FINISHED("TERMINADO", "Terminado"),
    PAUSED("PARADO", "Parado");

        // Función para obtener un WorkStatus a partir de su valor en String, y estado por defecto
        // Estado inicial por defecto para nuevos trabajos
    companion object {
        /**
         * Obtiene el estado desde el valor del backend
         */
        fun fromValue(value: String): WorkStatus? {
            return WorkStatus.entries.find { it.value == value }
        }

        /**
         * Estado inicial por defecto para nuevos trabajos
         */
        fun getDefaultStatus(): WorkStatus = PLASTER
    }
}