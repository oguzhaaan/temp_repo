package g11.reservationservice.controllers

import g11.reservationservice.dtos.PaymentLinkResponse
import g11.reservationservice.dtos.ReservationResponseDTO
import g11.reservationservice.dtos.ReservationTimeIntervalDTO
import g11.reservationservice.dtos.ReservationUpdateDTO
import g11.reservationservice.entities.enumerations.ReservationStatus
import g11.reservationservice.services.ReservationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@Tag(name = "Reservation")
@RestController
@RequestMapping("/api/v1/reservations")
class ReservationController(private val reservationService: ReservationService) {

    @Operation(summary = "Create a new reservation")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Reservation created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid request data"),
            ApiResponse(responseCode = "404", description = "User or car model not found"),
            ApiResponse(responseCode = "409", description = "Reservation conflict")
        ]
    )
    @PostMapping("/users/{customerId}/cars/{carModelId}")
    fun createReservation(
        @PathVariable customerId: Long,
        @PathVariable carModelId: Long,
        @Valid @RequestBody reservationTimeInterval: ReservationTimeIntervalDTO
    ): ResponseEntity<ReservationResponseDTO> {
        val savedReservation = reservationService.createReservation(customerId, carModelId, reservationTimeInterval)
        return ResponseEntity
            .created(reservationUri(savedReservation.reservationId))
            .body(savedReservation)
    }

    @Operation(summary = "Update reservation information (date and/or car model)")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Reservation updated successfully"),
            ApiResponse(responseCode = "400", description = "Invalid request data"),
            ApiResponse(responseCode = "404", description = "Reservation not found"),
            ApiResponse(responseCode = "409", description = "Reservation conflict")
        ]
    )
    @PutMapping("/{reservationId}")
    fun updateReservationInfo(
        @PathVariable reservationId: Long,
        @Valid @RequestBody updateRequest: ReservationUpdateDTO
    ): ResponseEntity<ReservationResponseDTO> {
        val updatedReservation = reservationService.updateReservationInfo(
            reservationId,
            updateRequest.carModelId,
            updateRequest.reservationTimeInterval
        )
        return ResponseEntity.ok(updatedReservation)
    }

    @Operation(summary = "Update reservation status (e.g., cancel)")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Reservation status updated successfully"),
            ApiResponse(responseCode = "404", description = "Reservation not found")
        ]
    )
    @PutMapping("/{reservationId}/status/{status}")
    fun updateReservationStatus(
        @PathVariable reservationId: Long,
        @PathVariable status: ReservationStatus
    ): ResponseEntity<ReservationResponseDTO> {
        val updatedReservation = reservationService.updateReservationStatus(reservationId, status)
        return ResponseEntity.ok(updatedReservation)
    }

    @Operation(summary = "Get reservation by ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Reservation found"),
            ApiResponse(responseCode = "404", description = "Reservation not found")
        ]
    )
    @GetMapping("/{reservationId}")
    fun getReservationById(
        @PathVariable reservationId: Long
    ): ResponseEntity<ReservationResponseDTO> {
        val reservation = reservationService.getReservationById(reservationId)
        return ResponseEntity.ok(reservation)
    }

    @Operation(summary = "Get all reservations by user ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Reservations found"),
            ApiResponse(responseCode = "404", description = "User not found")
        ]
    )
    @GetMapping("/users/{userId}")
    fun getAllReservationsByUserId(
        @PathVariable userId: Long
    ): ResponseEntity<List<ReservationResponseDTO>> {
        val reservations = reservationService.getAllReservationsByUserId(userId)
        return ResponseEntity.ok(reservations)
    }

    @Operation(summary = "Get all reservations")
    @ApiResponses(
        value = [ApiResponse(responseCode = "200", description = "Reservations retrieved")]
    )
    @GetMapping
    fun getAllReservations(): ResponseEntity<List<ReservationResponseDTO>> {
        val reservations = reservationService.getAllReservations()
        return ResponseEntity.ok(reservations)
    }

    @Operation(summary = "Delete a reservation")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Reservation deleted successfully"),
            ApiResponse(responseCode = "404", description = "Reservation not found")
        ]
    )
    @DeleteMapping("/{reservationId}")
    fun deleteReservation(
        @PathVariable reservationId: Long
    ): ResponseEntity<Unit> {
        reservationService.deleteReservation(reservationId)
        return ResponseEntity.noContent().build()
    }


    @Operation(summary = "Starts the payment flow for the given reservation ID and returns the approval URL")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Transfer assigned successfully"),
            ApiResponse(responseCode = "404", description = "Reservation not found")
        ]
    )
    @PostMapping("/{id}/pay")
    fun initiatePayment(@PathVariable id: Long): ResponseEntity<PaymentLinkResponse> {
        val paymentLink = reservationService.initiatePaymentForReservation(id)

        return ResponseEntity.ok(PaymentLinkResponse(paymentLink))
    }

    private fun reservationUri(id: Long) = URI.create("/api/v1/reservations/$id")
}

