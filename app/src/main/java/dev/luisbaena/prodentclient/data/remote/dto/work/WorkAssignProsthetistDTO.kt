package dev.luisbaena.prodentclient.data.remote.dto.work

import kotlinx.serialization.Serializable

    /**
     * DTO PARA ASIGNAR PROTESICO A TRABAJO
     * VER SI ES NECESARIO Y SI LO USAMOS ...
     */
@Serializable
data class WorkAssignProsthetistDTO(
    val protesicoId: String
)