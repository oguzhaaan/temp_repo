package g11.reservationservice.dtos

import g11.reservationservice.entities.enumerations.MaintenanceStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class MaintenanceHistoryRequestDTO(

    @field:NotNull(message = "Maintenance Status must be defined")
    var maintenanceStatus: MaintenanceStatus,

    var defect: String?,

    @field:NotBlank(message = "Service must be defined")
    var service: String,


    val maintenanceDescription: String?
)
