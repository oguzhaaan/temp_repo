package g11.reservationservice.dtos

data class CarModelResponseDTO(
    val id: Long,
    var brand: String,
    var model: String,
    var modelYear: Int,
    var segment: String,
    var numberOfDoors: Int,
    var seatingCapacity: Int,
    var luggageCapacity: Int,
    var category: String,
    var engineType: String,
    var transmissionType: String,
    var drivetrain: String,
    var motorDisplacement: Double,
    val features: MutableSet<String> = mutableSetOf(),
    var rentalPricePerDay: Float
)