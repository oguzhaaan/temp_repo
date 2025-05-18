package g11.reservationservice.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Dropoff(

    @OneToOne
    @MapsId
    @JoinColumn(name = "reservation_id")
    val reservation: Reservation,

    @Column(nullable = false)
    var timestamp: LocalDateTime,

    var location: String? = null,

    var handledByStaffId: Long? = null,

) : BaseEntity<Long>()
