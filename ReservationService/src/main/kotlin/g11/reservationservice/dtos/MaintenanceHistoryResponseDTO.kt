package g11.reservationservice.dtos

import g11.reservationservice.entities.enumerations.MaintenanceStatus
import java.time.LocalDateTime

data class MaintenanceHistoryResponseDTO(
    val id: Long,
    var vehicleId: Long,
    var maintenanceDate: LocalDateTime,
    var maintenanceStatus: MaintenanceStatus,
    var defect: String?,
    var service: String,
    val maintenanceDescription: String?
)
