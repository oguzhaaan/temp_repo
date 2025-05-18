package g11.reservationservice.entities

import jakarta.persistence.*


@Entity
@Table(name = "segment")
class Segment(
    @Id
    @Column(name = "name")
    var name: String
)