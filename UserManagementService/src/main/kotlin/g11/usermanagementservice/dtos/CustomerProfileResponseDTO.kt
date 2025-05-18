package g11.usermanagementservice.dtos

import g11.usermanagementservice.entities.enumerations.Preference
import java.time.LocalDate

data class CustomerProfileResponseDTO(
    val reliabilityScore: Float?,
    val drivingLicenseNumber: String,
    val drivingLicenseExpiry: LocalDate,
    val preferences: MutableSet<Preference>
)

