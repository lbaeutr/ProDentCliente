package dev.luisbaena.prodentclient.presentation.utils

import dev.luisbaena.prodentclient.domain.model.WorkStatus
import dev.luisbaena.prodentclient.domain.model.WorkStatusStatistics
import kotlin.collections.forEach

    /**
     * Agrupa las estadísticas por sección según el tipo de estado
     */
fun groupStatisticsBySection(statistics: List<WorkStatusStatistics>): Map<String, List<WorkStatusStatistics>> {
    val sections = mutableMapOf<String, MutableList<WorkStatusStatistics>>()

    statistics.forEach { stat ->
        val section = when (stat.status) {
            // Estado inicial
            WorkStatus.PLASTER -> {
                "Fase Inicial"
            }
            // Estados de Metal
            WorkStatus.METAL_LAB, WorkStatus.METAL_CAD, WorkStatus.METAL_TICARE,
            WorkStatus.METAL_CNC, WorkStatus.METAL_IDEA -> {
                "Producción Metal"
            }
            // Estados de Resina
            WorkStatus.RESIN_LAB, WorkStatus.RESIN_SH, WorkStatus.RESIN_ORTOHOMAX -> {
                "Producción Resina"
            }
            // Estados de clínica
            WorkStatus.CLINIC_CLIENT, WorkStatus.CLINIC_RODETES,
            WorkStatus.CLINIC_BIZCOCHO, WorkStatus.CLINIC_AESTHETIC -> {
                "Pruebas Clínicas"
            }
            // Estado de Cerámica
            WorkStatus.CERAMIC -> {
                "Producción Cerámica"
            }
            // Estados finales
            WorkStatus.FINISHED -> {
                "Finalizados"
            }
            // Estados especiales
            WorkStatus.CANCELLED, WorkStatus.PAUSED -> {
                "Estados Especiales"
            }
        }

        sections.getOrPut(section) { mutableListOf() }.add(stat)
    }

    // Ordenar las secciones en un orden específico
    val sectionOrder = listOf(
        "Fase Inicial",
        "Producción Metal",
        "Producción Resina",
        "Producción Cerámica",
        "Pruebas Clínicas",
        "Finalizados",
        "Estados Especiales"
    )

    return sectionOrder.mapNotNull { sectionName ->
        sections[sectionName]?.let { sectionName to it }
    }.toMap()
}