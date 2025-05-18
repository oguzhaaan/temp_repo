package g11.reservationservice.exceptions

import g11.reservationservice.advices.GlobalExceptionHandler
import g11.reservationservice.exceptions.*
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.core.MethodParameter
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.http.ResponseEntity
import org.springframework.http.ProblemDetail
import org.springframework.web.context.request.WebRequest
import java.lang.reflect.Method
import java.time.LocalDate
import java.util.NoSuchElementException

class GlobalExceptionHandlerTest {

    private val handler = GlobalExceptionHandler()

    @Test
    fun `handleNotFound should return 404`() {
        val reservationId = 1L
        val ex = ReservationNotFoundException(reservationId)
        val response = handler.handleNotFound(ex)
        assertEquals(404, response.statusCode.value())
        assertEquals("Reservation with id $reservationId not found", response.body!!.detail)
    }

    @Test
    fun `handleConflict should return 400`() {
        val ex = DuplicateLicensePlateException("Duplicate plate")
        val response = handler.handleConflict(ex)
        assertEquals(400, response.statusCode.value())
        assertEquals("Duplicated License Plate Duplicate plate", response.body!!.detail)
    }

    @Test
    fun `handleForbidden should return 403`() {
        val ex = UnauthorizedUserException("Unauthorized")
        val response = handler.handleForbidden(ex)
        assertEquals(403, response.statusCode.value())
        assertEquals("Unauthorized", response.body!!.detail)
    }

    @Test
    fun `handleBadRequest should return 400`() {
        val ex = InvalidDrivingLicenseException()
        val response = handler.handleBadRequest(ex)
        assertEquals(400, response.statusCode.value())
        assertEquals("Driving license must be valid for the reservation period.", response.body!!.detail)
    }

    @Test
    fun `handleIllegalArgument should return 400`() {
        val ex = IllegalArgumentException("Invalid argument")
        val response = handler.handleIllegalArgument(ex)
        assertEquals(400, response.status)
        assertEquals("Invalid argument", response.detail)
    }

    @Test
    fun `handleNoSuchElement should return 404`() {
        val ex = NoSuchElementException("No reservation found")
        val response = handler.handleNoSuchElement(ex)
        assertEquals(404, response.status)
        assertEquals("No reservation found", response.detail)
    }

    @Test
    fun `handleTypeMismatch for LocalDate should return custom date format message`() {
        val methodParam = mock(MethodParameter::class.java)
        val ex = MethodArgumentTypeMismatchException("invalid-date", LocalDate::class.java, "date", methodParam, null)

        val response = handler.handleTypeMismatch(ex)

        assertEquals(400, response.statusCodeValue)
        assertEquals("Date format must be yyyy-MM-dd", response.body!!["error"])
    }

    @Test
    fun `handleMissingParams should return missing parameter message`() {
        val ex = MissingServletRequestParameterException("startDate", "String")
        val response = handler.handleMissingParams(ex)
        assertEquals(400, response.statusCodeValue)
        assertEquals("Missing required parameter: startDate", response.body!!["error"])
    }

    @Test
    fun `handleUnexpected should return 500`() {
        val ex = RuntimeException("Something exploded")
        val request = mock(WebRequest::class.java)  //

        val response = handler.handleUnexpected(ex, request)

        assertEquals(500, response.statusCode.value())
        assertTrue(response.body!!.detail!!.contains("Something exploded"))
    }
}
