package g11.reservationservice.exceptions

class ReservationNotFoundException(reservationId: Long)
    : ApiException("Reservation with id $reservationId not found")