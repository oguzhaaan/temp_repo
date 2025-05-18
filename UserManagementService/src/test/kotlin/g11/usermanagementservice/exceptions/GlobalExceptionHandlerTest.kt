package g11.usermanagementservice.exceptions

import g11.usermanagementservice.advices.GlobalExceptionHandler
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.transaction.TransactionSystemException

class GlobalExceptionHandlerTest {

    private val handler = GlobalExceptionHandler()

    @Test
    fun `handleUserNotFound should return NOT_FOUND`() {
        val ex = UserNotFoundException(1)
        val response = handler.handleUserNotFound(ex)

        Assertions.assertEquals("User Not Found", response.title)
        Assertions.assertEquals(404, response.status)
        Assertions.assertTrue(response.detail?.contains("User") == true)
    }

    @Test
    fun `handleEmailExists should return CONFLICT`() {
        val ex = EmailExistsException("test@example.com")
        val response = handler.handleEmailExists(ex)

        Assertions.assertEquals("Email Conflict", response.title)
        Assertions.assertEquals(409, response.status)
        Assertions.assertTrue(response.detail?.contains("Email") == true)
    }

    @Test
    fun `handleUsernameExists should return CONFLICT`() {
        val ex = UsernameExistsException("username")
        val response = handler.handleUsernameExists(ex)

        Assertions.assertEquals("Username Conflict", response.title)
        Assertions.assertEquals(409, response.status)
        Assertions.assertTrue(response.detail?.contains("Username") == true)
    }

    @Test
    fun `handleInvalidUser should return BAD_REQUEST`() {
        val ex = InvalidUserException("Invalid user data")
        val response = handler.handleInvalidUser(ex)

        Assertions.assertEquals("Invalid User", response.title)
        Assertions.assertEquals(400, response.status)
        Assertions.assertTrue(response.detail?.contains("Invalid") == true)
    }

    @Test
    fun `handleIllegalArgument should return BAD_REQUEST`() {
        val ex = IllegalArgumentException("Invalid argument")
        val response = handler.handleIllegalArgument(ex)

        Assertions.assertEquals("Bad Request", response.title)
        Assertions.assertEquals(400, response.status)
        Assertions.assertTrue(response.detail?.contains("Invalid") == true)
    }

    @Test
    fun `handleIllegalState should return BAD_REQUEST`() {
        val ex = IllegalStateException("Invalid state")
        val response = handler.handleIllegalState(ex)

        Assertions.assertEquals("Bad Request", response.title)
        Assertions.assertEquals(400, response.status)
        Assertions.assertTrue(response.detail?.contains("Invalid") == true)
    }

    @Test
    fun `handleTransactionException should return BAD_REQUEST`() {
        val cause = RuntimeException("Transaction error")
        val ex = TransactionSystemException("Transaction failed", cause)
        val response = handler.handleTransactionException(ex)

        Assertions.assertEquals("Transaction Error", response.title)
        Assertions.assertEquals(400, response.status)
        Assertions.assertTrue(response.detail?.contains("Transaction") == true)
    }


}