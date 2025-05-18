package g11.reservationservice.mappers

import g11.reservationservice.dtos.MaintenanceHistoryRequestDTO
import g11.reservationservice.dtos.MaintenanceHistoryResponseDTO
import g11.reservationservice.entities.MaintenanceHistory
import g11.reservationservice.entities.Vehicle
import java.time.LocalDateTime
import java.util.*

fun MaintenanceHistory.toResponseDTO(vehicleId: Long)
        : MaintenanceHistoryResponseDTO = MaintenanceHistoryResponseDTO(
    this.getId(),
    vehicleId,
    this.maintenanceDate,
    this.maintenanceStatus,
    this.defect,
    this.service,
    this.maintenanceDescription
)

fun MaintenanceHistoryRequestDTO.toEntity(vehicle: Vehicle, maintenanceDate: LocalDateTime)
        : MaintenanceHistory = MaintenanceHistory(
    vehicle,
    maintenanceDate,
    this.maintenanceStatus,
    this.defect,
    this.service,
    this.maintenanceDescription
)
