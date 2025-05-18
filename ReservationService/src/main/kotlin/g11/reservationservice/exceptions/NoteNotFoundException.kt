package g11.reservationservice.exceptions

class NoteNotFoundException(id: Long) :
    ApiException("Note with ID $id not found")