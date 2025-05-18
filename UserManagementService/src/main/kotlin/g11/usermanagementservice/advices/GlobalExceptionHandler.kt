package g11.usermanagementservice.advices

import g11.usermanagementservice.exceptions.EmailExistsException
import g11.usermanagementservice.exceptions.InvalidUserException
import g11.usermanagementservice.exceptions.UserNotFoundException
import g11.usermanagementservice.exceptions.UsernameExistsException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.transaction.TransactionSystemException
import java.net.URI

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(ex: UserNotFoundException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.message ?: "User not found").apply {
            type = URI.create("https://api.example.com/errors/user-not-found")
            title = "User Not Found"
        }
    }

    @ExceptionHandler(EmailExistsException::class)
    fun handleEmailExists(ex: EmailExistsException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message ?: "Email already exists").apply {
            type = URI.create("https://api.example.com/errors/email-exists")
            title = "Email Conflict"
        }
    }

    @ExceptionHandler(UsernameExistsException::class)
    fun handleUsernameExists(ex: UsernameExistsException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message ?: "Username already exists").apply {
            type = URI.create("https://api.example.com/errors/username-exists")
            title = "Username Conflict"
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ProblemDetail> {
        val fieldErrors = ex.bindingResult.fieldErrors

        // Join all field errors into one string message for the "detail"
        val detailMessage = fieldErrors.joinToString("; ") {
            "${it.field}: ${it.defaultMessage}"
        }

        val errorsMap = fieldErrors.associate { it.field to it.defaultMessage }

        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detailMessage).apply {
            type = URI.create("https://api.example.com/errors/validation")
            title = "Validation Error"
            instance = URI.create("/users")
            properties?.set("errors", errorsMap)
        }

        return ResponseEntity.badRequest().body(problemDetail)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message ?: "Invalid request data").apply {
            type = URI.create("https://api.example.com/errors/illegal-argument")
            title = "Bad Request"
        }
    }

    @ExceptionHandler(InvalidUserException::class)
    fun handleInvalidUser(ex: InvalidUserException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message ?: "Invalid user data").apply {
            type = URI.create("https://api.example.com/errors/invalid-user")
            title = "Invalid User"
        }
    }

    @ExceptionHandler(TransactionSystemException::class)
    fun handleTransactionException(ex: TransactionSystemException): ProblemDetail {
        val cause = ex.rootCause
        val message = cause?.message ?: "Transaction failed due to invalid data"
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message).apply {
            type = URI.create("https://api.example.com/errors/transaction-error")
            title = "Transaction Error"
        }
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(ex: IllegalStateException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message ?: "Invalid request data").apply {
            type = URI.create("https://api.example.com/errors/illegal-argument")
            title = "Bad Request"
        }
    }


}

