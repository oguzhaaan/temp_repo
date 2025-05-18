package g11.reservationservice.exceptions

class MaintenanceNotBelongsToVehicleException (
    vehicleId: Long,
    maintenanceId: Long
) : ApiException("Maintenance history with id $maintenanceId does not belong to vehicle with id $vehicleId")