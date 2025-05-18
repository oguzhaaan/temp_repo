package g11.reservationservice.dtos

import g11.reservationservice.entities.enumerations.MaintenanceStatus
import java.time.LocalDate

data class MaintenanceHistoryFilterDTO(
    val status: List<MaintenanceStatus>? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null
)
