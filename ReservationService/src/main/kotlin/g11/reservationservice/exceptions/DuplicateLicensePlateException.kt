package g11.reservationservice.exceptions

class DuplicateLicensePlateException(licensePlate: String) :
    ApiException("Duplicated License Plate $licensePlate")