package g11.reservationservice.repositories

import g11.reservationservice.entities.MaintenanceHistory
import g11.reservationservice.entities.enumerations.MaintenanceStatus
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.time.LocalDate

interface MaintenanceHistoryRepository: JpaRepository<MaintenanceHistory, Long>,
    JpaSpecificationExecutor<MaintenanceHistory> {
    fun findAllByVehicleId(vehicleId: Long, pageable: Pageable): List<MaintenanceHistory>
    fun findByVehicleIdAndId(vehicleId: Long, id: Long): MaintenanceHistory?
    fun existsByVehicleIdAndMaintenanceStatusAndMaintenanceDateBetween(
        vehicleId: Long,
        status: MaintenanceStatus,
        startDate: LocalDate,
        endDate: LocalDate
    ): Boolean
}