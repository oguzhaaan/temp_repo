package g11.reservationservice.entities

import g11.reservationservice.entities.enumerations.ReservationStatus
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
class Reservation(

    @Column(name = "user_id", nullable = false)
    var userId: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    var vehicle: Vehicle,

    @Column(name = "start_date", nullable = false)
    var startDate: LocalDate,

    @Column(name = "end_date", nullable = false)
    var endDate: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_status", nullable = false)
    var reservationStatus: ReservationStatus,

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime,

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

    @Column(name = "total_price", nullable = false)
    var totalPrice: Float,

    @Column(name = "cancellation_date")
    var cancellationDate: LocalDateTime? = null,

    @OneToOne(mappedBy = "reservation", cascade = [CascadeType.ALL])
    var pickup: Pickup? = null,

    @OneToOne(mappedBy = "reservation", cascade = [CascadeType.ALL])
    var dropoff: Dropoff? = null,

) : BaseEntity<Long>()

