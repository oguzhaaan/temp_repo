package g11.reservationservice.exceptions

class UserNotFoundException(id: Long? = null) :
    ApiException(
        if (id != null) "User with ID $id not found"
        else "User with this info not found"
    )
