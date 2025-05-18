package g11.reservationservice.dtos

import java.time.LocalDateTime

data class TransferResponseDTO(
    val reservationId: Long,
    val vehicleId: Long,
    val location: String?,
    val transferTime: LocalDateTime?,
    val handledByStaffId: Long?,
)
