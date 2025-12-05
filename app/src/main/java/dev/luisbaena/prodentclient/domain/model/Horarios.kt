package dev.luisbaena.prodentclient.domain.model

    /**
     * Modelo de dominio para horarios de clínica
     */
data class Horarios(
    val lunes: String? = null,
    val martes: String? = null,
    val miercoles: String? = null,
    val jueves: String? = null,
    val viernes: String? = null
) {
    /**
     * Verifica si hay al menos un horario definido
     */
    fun hasHorarios(): Boolean {
        return lunes != null || martes != null || miercoles != null ||
               jueves != null || viernes != null
    }
    /**
     * Obtiene una lista de pares (día, horario) para los días que tienen horario
     */
    fun toListPairs(): List<Pair<String, String>> {
        return buildList {
            lunes?.let { add("Lunes" to it) }
            martes?.let { add("Martes" to it) }
            miercoles?.let { add("Miércoles" to it) }
            jueves?.let { add("Jueves" to it) }
            viernes?.let { add("Viernes" to it) }
        }
    }
}

