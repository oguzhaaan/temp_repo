package g11.usermanagementservice.exceptions

class EmailExistsException (email: String) : ApiException("Email ($email) already exists")