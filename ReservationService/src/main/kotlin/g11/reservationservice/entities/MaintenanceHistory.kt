package g11.reservationservice.entities

import g11.reservationservice.entities.enumerations.MaintenanceStatus
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

@Entity
@Table(name = "maintenance_history")
class MaintenanceHistory(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    var vehicle: Vehicle,

    @Column(name = "maintenance_date", nullable = false)
    @NotNull
    var maintenanceDate: LocalDateTime,

    @Enumerated(EnumType.STRING)
    @Column(name = "maintenance_status", nullable = false)
    var maintenanceStatus: MaintenanceStatus,

    @Column(name = "defect", nullable = true)
    var defect: String? = null,

    @Column(name = "service", nullable = false)
    @NotBlank
    var service: String,

    @Column(name = "maintenance_description", nullable = true)
    var maintenanceDescription: String? = null
) : BaseEntity<Long>()
