package g11.reservationservice.entities

import jakarta.persistence.*

@Entity
@Table(name = "feature")
class Feature(
    @Column(name = "name", nullable = false, unique = true)
    var name: String,
) : BaseEntity<Long>()
