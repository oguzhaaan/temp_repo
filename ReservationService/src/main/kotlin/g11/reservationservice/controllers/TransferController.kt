package g11.reservationservice.controllers

import g11.reservationservice.dtos.TransferDTO
import g11.reservationservice.dtos.TransferResponseDTO
import g11.reservationservice.entities.enumerations.TransferType
import g11.reservationservice.services.TransferService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("/api/v1/transfers")
@Tag(name = "Transfers", description = "Manage pickup and dropoff transfers")
class TransferController(
    private val transferService: TransferService
) {

    @Operation(
        summary = "Create a new transfer (pickup or dropoff)",
        responses = [
            ApiResponse(responseCode = "201", description = "Transfer created successfully"),
            ApiResponse(responseCode = "404", description = "Reservation not found"),
            ApiResponse(responseCode = "400", description = "Invalid transfer input")
        ]
    )
    @PostMapping("/{type}/{reservationId}")
    fun createTransfer(
        @PathVariable type: TransferType,
        @PathVariable reservationId: Long,
        @RequestBody @Valid transferDTO: TransferDTO,
        uriBuilder: UriComponentsBuilder
    ): ResponseEntity<TransferResponseDTO> {
        val created = transferService.createTransfer(type, reservationId, transferDTO)
        val uri = uriBuilder
            .path("/api/transfers/{type}/{reservationId}")
            .buildAndExpand(type.name.lowercase(), reservationId)
            .toUri()
        return ResponseEntity.created(uri).body(created)
    }

    @Operation(
        summary = "Update an existing transfer (pickup or dropoff)",
        responses = [
            ApiResponse(responseCode = "200", description = "Transfer updated successfully"),
            ApiResponse(responseCode = "404", description = "Reservation or transfer not found"),
            ApiResponse(responseCode = "400", description = "Invalid transfer update input")
        ]
    )
    @PutMapping("/{type}/{reservationId}")
    fun updateTransfer(
        @PathVariable type: TransferType,
        @PathVariable reservationId: Long,
        @RequestBody @Valid transferDTO: TransferDTO
    ): ResponseEntity<TransferResponseDTO> {
        val updated = transferService.updateTransfer(type, reservationId, transferDTO)
        return ResponseEntity.ok(updated)
    }

    @Operation(
        summary = "Delete a transfer (pickup or dropoff) by ID",
        responses = [
            ApiResponse(responseCode = "204", description = "Transfer deleted successfully"),
            ApiResponse(responseCode = "404", description = "Transfer not found")
        ]
    )
    @DeleteMapping("/{type}/{transferId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTransfer(
        @PathVariable type: TransferType,
        @PathVariable transferId: Long
    ) {
        transferService.deleteTransfer(type, transferId)
    }

    @Operation(
        summary = "Get transfer (pickup or dropoff) by reservation ID",
        responses = [
            ApiResponse(responseCode = "200", description = "Transfer found and returned"),
            ApiResponse(responseCode = "404", description = "Transfer or reservation not found")
        ]
    )
    @GetMapping("/{type}/{reservationId}")
    fun getTransferByReservationId(
        @PathVariable type: TransferType,
        @PathVariable reservationId: Long
    ): ResponseEntity<TransferResponseDTO> {
        val transfer = transferService.getTransferById(type, reservationId)
        return ResponseEntity.ok(transfer)
    }

    @Operation(
        summary = "Get all transfers (pickup or dropoff)",
        responses = [
            ApiResponse(responseCode = "200", description = "List of transfers returned")
        ]
    )
    @GetMapping("/{type}")
    fun getAllTransfers(@PathVariable type: TransferType): ResponseEntity<List<TransferResponseDTO>> {
        val transfers = transferService.getAllTransfers(type)
        return ResponseEntity.ok(transfers)
    }
}
