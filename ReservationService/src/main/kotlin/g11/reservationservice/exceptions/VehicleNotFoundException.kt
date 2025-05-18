package g11.reservationservice.exceptions

class VehicleNotFoundException(id: Long) :
    ApiException("Vehicle with ID $id not found")