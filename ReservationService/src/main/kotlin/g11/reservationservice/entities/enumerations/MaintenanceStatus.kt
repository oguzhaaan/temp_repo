package g11.reservationservice.entities.enumerations

enum class MaintenanceStatus (val displayName: String) {
    UPCOMING("Upcoming"),
    CLEANING("Cleaning"),
    REPAIRING("Repairing"),
    WAITING_FOR_PARTS("Waiting for Parts"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}