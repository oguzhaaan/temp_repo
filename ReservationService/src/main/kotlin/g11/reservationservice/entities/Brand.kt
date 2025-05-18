package g11.reservationservice.entities

import jakarta.persistence.*

@Entity
@Table(name = "brand")
class Brand(
    @Id
    @Column(name = "name")
    var name: String
)