package g11.usermanagementservice.dtos

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*
import java.time.LocalDate

data class UserRequestDTO(

    @field:Schema(description = "Unique username (3-50 characters, letters, numbers, hyphens or underscores)", example = "john_doe123")
    @field:NotBlank(message = "Username cannot be blank")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9_-]{3,50}$",
        message = "Username must contain only letters, numbers, hyphens, or underscores, and be between 3 and 50 characters."
    )
    val username: String,

    @field:Schema(description = "First name of the user", example = "John")
    @field:NotBlank(message = "First name cannot be blank")
    @field:Pattern(
        regexp = "^[a-zA-Z\\s]{1,50}$",
        message = "First name must contain only letters and spaces, and be between 1 and 50 characters."
    )
    val firstName: String,

    @field:Schema(description = "Last name of the user", example = "Doe")
    @field:NotBlank(message = "Last name cannot be blank")
    @field:Pattern(
        regexp = "^[a-zA-Z\\s]{1,50}$",
        message = "Last name must contain only letters and spaces, and be between 1 and 50 characters."
    )
    val lastName: String,

    @field:Schema(description = "Valid email address", example = "john.doe@example.com")
    @field:NotBlank(message = "Email cannot be blank")
    @field:Email(message = "Invalid email format")
    val email: String,

    @field:Schema(description = "Phone number with 10-15 digits (optionally starts with '+')", example = "+12345678901")
    @field:NotBlank(message = "Phone number cannot be blank")
    @field:Pattern(
        regexp = "\\+?[0-9]{10,15}",
        message = "Phone number must be between 10 and 15 digits, optionally starting with '+'"
    )
    val phoneNumber: String,

    @field:Schema(description = "Date of birth (must be in the past)", example = "1995-06-15")
    @field:Past(message = "Birthdate must be in the past")
    val birthDate: LocalDate,

    @field:Schema(description = "Optional customer profile details")
    val customerProfile: CustomerProfileRequestDTO? = null
)
