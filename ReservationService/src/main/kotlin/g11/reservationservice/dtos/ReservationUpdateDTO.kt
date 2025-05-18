package g11.reservationservice.dtos

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid

data class ReservationUpdateDTO(
    @Schema(description = "Optional. If provided, system will try to assign a vehicle from this car model, else the car model will be preserved.")
    val carModelId: Long?, // nullable → optional update
    @field:Valid val reservationTimeInterval: ReservationTimeIntervalDTO
)
