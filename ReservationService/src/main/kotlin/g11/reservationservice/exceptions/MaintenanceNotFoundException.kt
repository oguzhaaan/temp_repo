package g11.reservationservice.exceptions

class MaintenanceNotFoundException (id: Long) :
    ApiException("Maintenance history with ID $id not found")