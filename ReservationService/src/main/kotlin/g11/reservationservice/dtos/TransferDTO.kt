package g11.reservationservice.dtos

import java.time.LocalDateTime

data class TransferDTO(
    val transferTime: LocalDateTime,
    val location: String? = null,
    val handledByStaffId: Long? = null,
)

