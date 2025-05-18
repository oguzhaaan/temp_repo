package g11.reservationservice.repositories

import g11.reservationservice.entities.BrandModel
import g11.reservationservice.entities.CarModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface CarModelRepository : JpaRepository<CarModel, Long>, JpaSpecificationExecutor<CarModel> {
    fun findByBrandModelAndModelYear(brandModel: BrandModel, modelYear: Int): List<CarModel>
    @Query("""
    SELECT DISTINCT cm FROM CarModel cm
    JOIN cm.vehicles v
    WHERE EXISTS (
        SELECT 1 FROM Vehicle v2
        WHERE v2.carModel = cm
        AND v2.vehicleStatus = 'AVAILABLE'
        AND NOT EXISTS (
            SELECT 1 FROM MaintenanceHistory mh
            WHERE mh.vehicle = v2
            AND mh.maintenanceStatus = 'UPCOMING'
            AND mh.maintenanceDate BETWEEN :startDate AND :endDate
        )
        AND NOT EXISTS (
            SELECT 1 FROM Reservation r
            WHERE r.vehicle = v2
            AND r.reservationStatus <> 'CANCELLED'
            AND (
                (r.startDate <= :endDate AND r.endDate >= :startDate)
            )
        )
    )
""")
    fun findAvailableCarModelsDuring(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<CarModel>
}