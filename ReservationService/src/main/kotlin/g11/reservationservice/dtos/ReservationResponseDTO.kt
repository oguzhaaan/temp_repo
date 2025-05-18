package g11.reservationservice.dtos

import g11.reservationservice.entities.enumerations.ReservationStatus
import java.time.LocalDate
import java.time.LocalDateTime

data class ReservationResponseDTO(
    val reservationId: Long,
    val userId: Long,
    val vehicle: VehicleResponseDTO,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val reservationStatus: ReservationStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val totalPrice: Float,
    val cancellationDate: LocalDateTime?,
    val pickup: TransferDTO? = null,
    val dropoff: TransferDTO? = null,
)
