package g11.usermanagementservice.entities

import g11.usermanagementservice.entities.enumerations.Preference
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "customer_profiles")
class CustomerProfile(

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    var user: User,

    var reliabilityScore: Float? = null,

    @Column(nullable = false)
    var drivingLicenseNumber: String,

    @Column(nullable = false)
    var drivingLicenseExpiry: LocalDate,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "customer_preferences",
        joinColumns = [JoinColumn(name = "customer_profile_id")],
        uniqueConstraints = [UniqueConstraint(columnNames = ["customer_profile_id", "preference"])]
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "preference", nullable = false)
    var preferences: MutableSet<Preference> = mutableSetOf()

) : BaseEntity<Long>()

