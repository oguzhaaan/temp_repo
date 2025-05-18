package g11.usermanagementservice.exceptions

class UsernameExistsException (username: String) : ApiException(
    "Username $username already exists",)