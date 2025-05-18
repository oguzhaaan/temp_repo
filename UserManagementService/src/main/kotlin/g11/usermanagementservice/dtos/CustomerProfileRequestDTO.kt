package g11.usermanagementservice.dtos

import g11.usermanagementservice.entities.enumerations.Preference
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import java.time.LocalDate


data class CustomerProfileRequestDTO(

    @field:NotBlank(message = "Driving license number cannot be blank")
    @field:Pattern(
        regexp = "^[A-Z0-9]{8,20}$",
        message = "Driving license number must be 8 to 20 uppercase letters or digits."
    )
    val drivingLicenseNumber: String,

    @field:Future(message = "Driving license expiry must be in the future")
    val drivingLicenseExpiry: LocalDate,

    val preferences: MutableSet<Preference>
)


