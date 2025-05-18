package g11.reservationservice.dtos

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotNull
import java.time.LocalDate


data class ReservationTimeIntervalDTO(
    @field:NotNull
    @field:Future
    val startDate: LocalDate,

    @field:NotNull
    @field:Future
    val endDate: LocalDate,
) {
    init {
        require(startDate.isAfter(LocalDate.now())) { "Start date must be in the future." }
        require(endDate.isAfter(startDate)) { "End date must be after start date." }
    }
}

