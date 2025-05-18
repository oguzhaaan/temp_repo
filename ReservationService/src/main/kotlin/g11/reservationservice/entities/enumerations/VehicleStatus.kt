package g11.reservationservice.entities.enumerations

enum class VehicleStatus (val displayName: String) {
    AVAILABLE("Available"),
    RENTED("Rented"),
    UNDER_MAINTENANCE("Under Maintenance"),
}