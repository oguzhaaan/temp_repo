package g11.reservationservice.repositories

import g11.reservationservice.entities.Reservation
import g11.reservationservice.entities.enumerations.ReservationStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface ReservationRepository: JpaRepository<Reservation, Long> {
    fun findAllByUserId(userId: Long): List<Reservation>

    fun existsByVehicleIdAndReservationStatusNotAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
        vehicleId: Long,
        excludedStatus: ReservationStatus,
        endDate: LocalDate,
        startDate: LocalDate
    ): Boolean

    fun existsByVehicleIdAndReservationStatusNotAndIdNotAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
        vehicleId: Long,
        excludedStatus: ReservationStatus,
        excludeId: Long,
        endDate: LocalDate,
        startDate: LocalDate
    ): Boolean
}