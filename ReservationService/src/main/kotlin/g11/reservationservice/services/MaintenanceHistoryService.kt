package g11.reservationservice.services

import g11.reservationservice.dtos.MaintenanceHistoryFilterDTO
import g11.reservationservice.dtos.MaintenanceHistoryRequestDTO
import g11.reservationservice.dtos.MaintenanceHistoryResponseDTO
import g11.reservationservice.dtos.ReservationTimeIntervalDTO
import g11.reservationservice.entities.enumerations.MaintenanceStatus
import java.time.LocalDateTime

interface MaintenanceHistoryService {

    fun createMaintenanceHistory(
        vehicleId: Long,
        maintenanceHistory: MaintenanceHistoryRequestDTO,
        date: LocalDateTime? = null
    ): MaintenanceHistoryResponseDTO

    fun updateMaintenanceHistory(
        vehicleId: Long,
        id: Long,
        newMaintenanceHistory: MaintenanceHistoryRequestDTO,
        date: LocalDateTime?
    ): MaintenanceHistoryResponseDTO

    fun findAllByVehicleId(
        vehicleId: Long,
        page: Int,
        size: Int,
        filter: MaintenanceHistoryFilterDTO
    ): List<MaintenanceHistoryResponseDTO>

    fun findByVehicleIdAndId(vehicleId: Long, id: Long): MaintenanceHistoryResponseDTO
    fun deleteMaintenanceHistory(vehicleId: Long, id: Long)
    fun ifMaintenanceExists(
        vehicleId: Long,
        status: MaintenanceStatus,
        timeIntervalDTO: ReservationTimeIntervalDTO,
    ): Boolean

}