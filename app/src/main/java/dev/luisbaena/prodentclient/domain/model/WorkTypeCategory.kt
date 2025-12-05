package dev.luisbaena.prodentclient.domain.model


    /**
     * Enum de categorías válidas para tipos de trabajo
     * DEBE COINCIDIR CON EL BACKEND
     */
enum class WorkTypeCategory(
    val value: String,
    val displayName: String
) {
    PROTESIS_FIJA(
        "PROTESIS_FIJA",
        "Prótesis Fija"
    ),
    PROTESIS_REMOVIBLE(
        "PROTESIS_REMOVIBLE",
        "Prótesis Removible"
    ),
    FERULA(
        "FERULA",
        "Férula"
    ),
    TRABAJO_RESINA(
        "TRABAJO_RESINA",
        "Trabajo de Resina"
    ),
    PROTESIS_IMPLANTE(
        "PROTESIS_IMPLANTE",
        "Prótesis sobre Implante"
    );

        // Funciones de utilidad para WorkTypeCategory
    companion object {
        /**
         * Obtiene la categoría desde el valor del backend
         */
        fun fromValue(value: String): WorkTypeCategory? {
            return entries.find { it.value == value }
        }

        /**
         * Devuelve el nombre legible de una categoría de trabajo
         * Convierte valores del backend (ej: "PROTESIS_IMPLANTE") a nombres mostrados (ej: "Prótesis sobre Implante")
         */
        fun getDisplayName(category: String): String {
            return fromValue(category)?.displayName ?: category
        }

        /**
         * Obtiene todas las categorías como lista de nombres para mostrar
         */
        fun getAllDisplayNames(): List<String> {
            return entries.map { it.displayName }
        }

        /**
         * Obtiene todas las categorías como lista de valores del backend
         */
        fun getAllValues(): List<String> {
            return entries.map { it.value }
        }

        /**
         * Valida si una categoría es válida
         */
        fun isValid(value: String): Boolean {
            return entries.any { it.value == value }
        }

        /**
         * Obtiene pares (valor, displayName) para dropdowns, para futuros usos
         */
        fun getAllPairs(): List<Pair<String, String>> {
            return entries.map { it.value to it.displayName }
        }
    }
}