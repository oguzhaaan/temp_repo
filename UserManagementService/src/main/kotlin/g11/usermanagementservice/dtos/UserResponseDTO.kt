package g11.usermanagementservice.dtos

import g11.usermanagementservice.entities.enumerations.Role
import java.time.LocalDate

data class UserResponseDTO(
    val id: Long,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val role: Role,
    val birthDate: LocalDate,
    val customerProfile: CustomerProfileResponseDTO? = null
)
