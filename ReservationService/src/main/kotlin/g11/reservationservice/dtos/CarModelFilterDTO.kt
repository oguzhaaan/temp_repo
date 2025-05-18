package g11.reservationservice.dtos

import g11.reservationservice.entities.enumerations.Category
import g11.reservationservice.entities.enumerations.Drivetrain
import g11.reservationservice.entities.enumerations.EngineType
import g11.reservationservice.entities.enumerations.TransmissionType

data class CarModelFilterDTO(
    val brandModels: List<String>? = null,
    val features: List<String>? = null,
    val segments: List<String>? = null,
    val minRentalPrice: Float? = null,
    val maxRentalPrice: Float? = null,

    val modelYears: List<Int>? = null,
    val categories: List<Category>? = null,
    val engineTypes: List<EngineType>? = null,
    val transmissionTypes: List<TransmissionType>? = null,
    val drivetrains: List<Drivetrain>? = null,
)

