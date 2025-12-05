package dev.luisbaena.prodentclient.domain.usecase.work

import dev.luisbaena.prodentclient.data.remote.dto.work.WorkDetailDto
import dev.luisbaena.prodentclient.data.remote.dto.work.WorkDirectPaymentDTO
import dev.luisbaena.prodentclient.domain.repository.WorkRepository
import javax.inject.Inject

    /**
     * UseCase para marcar un trabajo como pagado
     */
class MarkWorkAsPaidUseCase @Inject constructor(
    private val workRepository: WorkRepository
) {
    suspend operator fun invoke(id: String, payment: WorkDirectPaymentDTO): Result<WorkDetailDto> {
        return workRepository.markAsPaid(id, payment)
    }
}