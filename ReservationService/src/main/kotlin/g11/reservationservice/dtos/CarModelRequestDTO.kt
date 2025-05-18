package g11.reservationservice.dtos

import g11.reservationservice.entities.enumerations.Category
import g11.reservationservice.entities.enumerations.Drivetrain
import g11.reservationservice.entities.enumerations.EngineType
import g11.reservationservice.entities.enumerations.TransmissionType
import jakarta.validation.constraints.*

data class CarModelRequestDTO(

    @field:NotBlank
    @field:Size(min = 1, max = 50, message = "Brand name must be between 1 and 50 characters")
    val brand: String,

    @field:NotBlank
    @field:Size(min = 1, max = 50, message = "Model name must be between 1 and 50 characters")
    val model: String,


    @field:NotNull
    @field:Min(1886, message = "Model year must be 1886 or later") // The first car was invented in 1886
    val modelYear: Int,


    @field:NotNull
    val segment: String,

    @field:NotNull
    @field:Min(value = 2, message = "A car must have at least 2 doors")
    val numberOfDoors: Int,

    @field:NotNull
    @field:Min(value = 2, message = "Seating capacity must be at least 2")
    val seatingCapacity: Int,

    @field:NotNull
    @field:Min(value = 0, message = "Luggage capacity cannot be negative")
    val luggageCapacity: Int,

    @field:NotNull
    val category: Category,

    @field:NotNull
    val engineType: EngineType,

    @field:NotNull
    val transmissionType: TransmissionType,

    @field:NotNull
    val drivetrain: Drivetrain,

    @field:NotNull
    @field:Min(value = 0, message = "Motor displacement cannot be negative")
    val motorDisplacement: Double,

    @field:NotEmpty(message = "Features cannot be empty")
    val features: Set<String> = emptySet(),

    @field:NotNull
    @field:Min(0)
    val rentalPricePerDay: Float
)