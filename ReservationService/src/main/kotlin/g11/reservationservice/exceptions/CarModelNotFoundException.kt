package g11.reservationservice.exceptions

class CarModelNotFoundException(id: Long) :
    ApiException("Car model with ID $id not found")
