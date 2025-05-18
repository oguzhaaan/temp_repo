package g11.reservationservice.dtos

import g11.reservationservice.entities.enumerations.Category
import g11.reservationservice.entities.enumerations.Drivetrain
import g11.reservationservice.entities.enumerations.EngineType
import g11.reservationservice.entities.enumerations.TransmissionType

data class CarModelUpdateDTO(
    val brand: String?, // nullable
    val model: String?,

    val modelYear: Int?,
    val segment: String?,
    val numberOfDoors: Int?,
    val seatingCapacity: Int?,
    val luggageCapacity: Int?,

    val category: Category?,
    val engineType: EngineType?,
    val transmissionType: TransmissionType?,
    val drivetrain: Drivetrain?,
    val motorDisplacement: Double?,

    val features: Set<String>?,
    val rentalPricePerDay: Float?
)

