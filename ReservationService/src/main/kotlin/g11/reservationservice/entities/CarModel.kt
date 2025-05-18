package g11.reservationservice.entities

import g11.reservationservice.entities.enumerations.Category
import g11.reservationservice.entities.enumerations.Drivetrain
import g11.reservationservice.entities.enumerations.EngineType
import g11.reservationservice.entities.enumerations.TransmissionType
import jakarta.persistence.*

@Table(name = "car_model")
@Entity
class CarModel(
    // BASIC DETAILS

    @ManyToOne
    @JoinColumn(name = "brand_model_id", nullable = false)
    var brandModel: BrandModel,

    @Column(name = "model_year", nullable = false)
    var modelYear: Int,

    @ManyToOne
    @JoinColumn(name = "segment", nullable = false)
    var segment: Segment,

    @Column(name = "number_of_doors", nullable = false)
    var numberOfDoors: Int,

    @Column(name = "seating_capacity", nullable = false)
    var seatingCapacity: Int,

    @Column(name = "luggage_capacity", nullable = false)
    var luggageCapacity: Int,

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    var category: Category,

    // TECHNICAL SPECIFICATIONS

    @Enumerated(EnumType.STRING)
    @Column(name = "engine_type", nullable = false)
    var engineType: EngineType,

    @Enumerated(EnumType.STRING)
    @Column(name = "transmission_type", nullable = false)
    var transmissionType: TransmissionType,

    @Enumerated(EnumType.STRING)
    @Column(name = "drivetrain", nullable = false)
    var drivetrain: Drivetrain,

    @Column(name = "motor_displacement", nullable = false)
    var motorDisplacement: Double,

    // FEATURES & EXTRAS

    @ManyToMany
    @JoinTable(
        name = "car_model_feature",
        joinColumns = [JoinColumn(name = "car_model_id")],
        inverseJoinColumns = [JoinColumn(name = "feature_id")]
    )
    val features: MutableSet<Feature> = mutableSetOf(),

    // RENTAL PRICING

    @Column(name = "rental_price_per_day", nullable = false)
    var rentalPricePerDay: Float
) : BaseEntity<Long>() {
    @OneToMany(mappedBy = "carModel", cascade = [CascadeType.ALL], orphanRemoval = true)
    val vehicles: MutableSet<Vehicle> = mutableSetOf()
}