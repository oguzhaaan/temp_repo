package g11.reservationservice.entities.enumerations

enum class ReservationStatus(val displayName: String) {
    ONGOING("Ongoing"),
    WAITING_FOR_PAYMENT("Waiting for payment"),
    CONFIRMED("Confirmed"),
    CANCELLED("Cancelled"),
    COMPLETED("Completed");
}