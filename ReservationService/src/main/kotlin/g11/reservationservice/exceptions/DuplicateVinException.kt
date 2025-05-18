package g11.reservationservice.exceptions

class DuplicateVinException(vin: String) :
    ApiException("Duplicated Vin $vin")