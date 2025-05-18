package g11.reservationservice.entities

import jakarta.persistence.*

@Entity
@Table(name = "brand_model")
data class BrandModel(
    @ManyToOne
    @JoinColumn(name = "brand", nullable = false)
    var brand: Brand,

    @Column(name = "model", nullable = false)
    var model: String
) : BaseEntity<Long>()