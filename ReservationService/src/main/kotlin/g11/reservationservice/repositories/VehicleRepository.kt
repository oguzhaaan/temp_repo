package g11.reservationservice.repositories

import g11.reservationservice.entities.Vehicle
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface VehicleRepository: JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {
    fun existsByLicensePlate(licensePlate: String): Boolean
    fun existsByVin(vin: String): Boolean
    fun existsByLicensePlateAndIdNot(licensePlate: String, id: Long): Boolean
    fun existsByVinAndIdNot(vin: String, id: Long): Boolean

    @Query("""
    SELECT v FROM Vehicle v
    WHERE v.carModel.id = :carModelId
    AND v.vehicleStatus = 'AVAILABLE'
    AND NOT EXISTS (
        SELECT 1 FROM MaintenanceHistory mh
        WHERE mh.vehicle = v
        AND mh.maintenanceStatus = 'UPCOMING'
        AND mh.maintenanceDate BETWEEN :startDate AND :endDate
    )
    AND NOT EXISTS (
        SELECT 1 FROM Reservation r
        WHERE r.vehicle = v
        AND r.reservationStatus <> 'CANCELLED'
        AND (
            r.startDate <= :endDate AND r.endDate >= :startDate
        )
    )
    ORDER BY v.id ASC
""")
    fun findFirstAvailableVehicleForReservation(
        @Param("carModelId") carModelId: Long,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<Vehicle>
}