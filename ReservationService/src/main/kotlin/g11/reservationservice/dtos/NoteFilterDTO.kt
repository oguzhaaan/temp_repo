package g11.reservationservice.dtos

import java.time.LocalDate

data class NoteFilterDTO(
    val author: String? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null
)

