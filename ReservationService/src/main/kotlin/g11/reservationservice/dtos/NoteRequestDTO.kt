package g11.reservationservice.dtos

import g11.reservationservice.entities.enumerations.NoteType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class NoteRequestDTO(
    @field:NotBlank(message = "Note must be defined")
    val note: String,

    @field:NotBlank(message = "Author must be defined")
    val author: String,

    @field:NotNull(message = "Type must be defined")
    @Schema(description = "Available types: GENERAL, MAINTENANCE, RESERVATION, RESERVATION_CANCEL, RESERVATION_PICKUP, RESERVATION_DROPOFF")
    val type: NoteType
)
