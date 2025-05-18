package g11.reservationservice.dtos

import g11.reservationservice.entities.enumerations.NoteType
import java.time.LocalDateTime

data class NoteResponseDTO(
    val id: Long,
    val vehicleId: Long,
    val note: String,
    val date: LocalDateTime,
    val author: String,
    val type: NoteType
)
