package g11.reservationservice.exceptions

class NoteNotBelongsToVehicleException(
    vehicleId: Long,
    noteId: Long
) : ApiException("Note with id $noteId does not belong to vehicle with id $vehicleId")