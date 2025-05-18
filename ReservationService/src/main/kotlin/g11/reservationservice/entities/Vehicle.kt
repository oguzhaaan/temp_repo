package g11.reservationservice.entities

import g11.reservationservice.entities.enumerations.VehicleStatus
import jakarta.persistence.*
import jakarta.validation.constraints.Min

@Entity
@Table(name = "vehicle")
class Vehicle(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_model_id", nullable = false)
    var carModel: CarModel,

    @Column(name = "license_plate", nullable = false, unique = true)
    var licensePlate: String,

    @Column(name = "vin", nullable = false, unique = true)
    var vin: String,

    @Column(name = "kilometers_travelled", nullable = false)
    @Min(0)
    var kilometersTravelled: Int,

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_status", nullable = false)
    var vehicleStatus: VehicleStatus,
) : BaseEntity<Long>() {
    @OneToMany(mappedBy = "vehicle", cascade = [CascadeType.ALL], orphanRemoval = true)
    val notes: MutableSet<Note> = mutableSetOf()

    @OneToMany(mappedBy = "vehicle", cascade = [CascadeType.ALL], orphanRemoval = true)
    val maintenanceHistories: MutableSet<MaintenanceHistory> = mutableSetOf()

    @OneToMany(mappedBy = "vehicle", cascade = [CascadeType.ALL], orphanRemoval = true)
    val reservations: MutableSet<Reservation> = mutableSetOf()
}
