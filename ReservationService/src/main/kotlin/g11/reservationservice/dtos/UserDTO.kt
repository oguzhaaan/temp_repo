package g11.reservationservice.dtos

import java.time.LocalDate

data class UserDTO(
    val id: Long,
    val role: String,
    val customerProfile: CustomerProfileDTO?
)

data class CustomerProfileDTO(
    val drivingLicenseExpiry: LocalDate
)

