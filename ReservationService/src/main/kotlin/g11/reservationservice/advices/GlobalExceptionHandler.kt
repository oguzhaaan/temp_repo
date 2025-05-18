package g11.reservationservice.advices


import g11.reservationservice.exceptions.*
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.time.LocalDate
import java.util.NoSuchElementException

@RestControllerAdvice
class GlobalExceptionHandler {

    // --- Not Found Exceptions ---
    @ExceptionHandler(
        CarModelNotFoundException::class,
        VehicleNotFoundException::class,
        NoteNotFoundException::class,
        MaintenanceNotFoundException::class,
        ReservationNotFoundException::class,
        UserNotFoundException::class,
        NoAvailableVehicleException::class,
        TransferNotFoundException::class,
    )
    fun handleNotFound(ex: RuntimeException): ResponseEntity<ProblemDetail> {
        val detail = ex.message ?: "Resource not found"
        val problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, detail)
        return ResponseEntity(problem, HttpStatus.NOT_FOUND)
    }

    // --- Conflict / Duplicate Exceptions ---
    @ExceptionHandler(
        DuplicateModelException::class,
        DuplicateLicensePlateException::class,
        DuplicateVinException::class
    )
    fun handleConflict(ex: RuntimeException): ResponseEntity<ProblemDetail> {
        val problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message ?: "Duplicate resource")
        return ResponseEntity(problem, HttpStatus.BAD_REQUEST)
    }

    // --- Authorization / Ownership Exceptions ---
    @ExceptionHandler(
        UnauthorizedUserException::class,
        NoteNotBelongsToVehicleException::class,
        MaintenanceNotBelongsToVehicleException::class
    )
    fun handleForbidden(ex: RuntimeException): ResponseEntity<ProblemDetail> {
        val problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.message ?: "Access denied")
        return ResponseEntity(problem, HttpStatus.FORBIDDEN)
    }

    // --- Business Logic Errors ---
    @ExceptionHandler(
        InvalidDrivingLicenseException::class,
        InvalidReservationStatusException::class,
        MissingCustomerProfileException::class,
        TransferDateMismatchException::class
    )
    fun handleBadRequest(ex: RuntimeException): ResponseEntity<ProblemDetail> {
        val problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message ?: "Invalid request")
        return ResponseEntity(problem, HttpStatus.BAD_REQUEST)
    }

    // --- Spring Validation Exceptions ---
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ProblemDetail> {
        val errors = ex.bindingResult.allErrors
            .mapNotNull { (it as? ObjectError)?.defaultMessage }
            .takeIf { it.isNotEmpty() }
            ?.joinToString(", ") ?: "Validation failed"

        val problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errors)
        return ResponseEntity(problem, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<Map<String, String>> {
        val message = if (ex.requiredType == LocalDate::class.java)
            "Date format must be yyyy-MM-dd"
        else
            "Invalid value for parameter '${ex.name}'"

        return ResponseEntity.badRequest().body(mapOf("error" to message))
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParams(ex: MissingServletRequestParameterException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.badRequest().body(mapOf("error" to "Missing required parameter: ${ex.parameterName}"))
    }

    // --- Illegal Arguments and Missing Elements ---
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ProblemDetail {
        val message = ex.message ?: "Invalid argument"
        val (status, title) = when {
            "Brand" in message -> HttpStatus.CONFLICT to "Brand already exists"
            "Feature" in message -> HttpStatus.CONFLICT to "Feature already exists"
            else -> HttpStatus.BAD_REQUEST to "Invalid request"
        }
        return ProblemDetail.forStatus(status).apply {
            this.title = title
            this.detail = message
        }
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElement(ex: NoSuchElementException): ProblemDetail {
        val message = ex.message ?: "No such element"
        val title = if ("Brand Model" in message) "Brand Model Not Found" else "Resource Not Found"
        return ProblemDetail.forStatus(HttpStatus.NOT_FOUND).apply {
            this.title = title
            this.detail = message
        }
    }

    // --- Catch-All ---
    @ExceptionHandler(Exception::class)
    fun handleUnexpected(ex: Exception, request: WebRequest): ResponseEntity<ProblemDetail> {
        val detail = "An unexpected error occurred: ${ex.message}"
        val problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, detail)
        return ResponseEntity(problem, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}

