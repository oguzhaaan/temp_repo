package g11.paymentservice.controllers

import g11.paymentservice.dtos.PaymentConfirmationRequest
import g11.paymentservice.dtos.PaymentInitiationRequest
import g11.paymentservice.dtos.PaymentInitiationResponse
import g11.paymentservice.services.PaymentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Payment Service", description = "Payment API")
@RestController
@RequestMapping("/payments")
class PaymentController(
    private val paymentService: PaymentService
) {
    @Operation(
        summary = "Initiate a payment",
        description = "Initiate a payment using the provided request details."
    )
    @PostMapping("/initiate")
    fun initiatePayment(@RequestBody request: PaymentInitiationRequest): ResponseEntity<PaymentInitiationResponse> {
        val response = paymentService.initiatePayment(request)
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "Confirm a payment",
        description = "Confirm a payment using the provided order ID."
    )
    @GetMapping("/confirm")
    fun confirmPayment(@RequestParam("token") orderId: String): ResponseEntity<String> {
        val result = paymentService.confirmPayment(PaymentConfirmationRequest(orderId))

        val reservationId = result.reservationId

        return if (result.success) {
            // Successfully confirmed payment
            ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, "http://localhost:8080/payment-result?Success=true&reservationId=$reservationId")
                .build()
        } else {
            // In case of failure, redirect to the dashboard or failure page
            ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, "http://localhost:8080/payment-result?Success=false&reservationId=$reservationId")
                .build()
        }
    }

}
