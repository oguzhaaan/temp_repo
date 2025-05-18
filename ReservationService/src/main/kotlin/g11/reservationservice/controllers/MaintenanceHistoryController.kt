package g11.reservationservice.controllers

import g11.reservationservice.dtos.MaintenanceHistoryFilterDTO
import g11.reservationservice.dtos.MaintenanceHistoryRequestDTO
import g11.reservationservice.dtos.MaintenanceHistoryResponseDTO
import g11.reservationservice.entities.enumerations.MaintenanceStatus
import g11.reservationservice.services.MaintenanceHistoryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.time.LocalDate
import java.time.LocalDateTime


@Tag(name = "Maintenance History", description = "Maintenance History API")
@RestController
@RequestMapping("/api/v1/vehicles/{vehicleId}/maintenances")
class MaintenanceHistoryController(private val maintenanceHistoryService: MaintenanceHistoryService) {

    @Operation(summary = "Find maintenance histories for a specific vehicle, with optional filters")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "List of notes retrieved"),
            ApiResponse(responseCode = "400", description = "Invalid request parameters (e.g., incorrect date format)"),
            ApiResponse(responseCode = "404", description = "Vehicle not found")
        ]
    )
    @GetMapping("/")
    fun getAllMaintenances(
        @PathVariable vehicleId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) status: List<MaintenanceStatus>?,

        @Parameter(
            description = "Start date for the maintenance history filter. Expected format: yyyy-MM-dd",
            schema = Schema(type = "string", format = "date")
        )
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate?,

        @Parameter(
            description = "End date for the maintenance history filter. Expected format: yyyy-MM-dd",
            schema = Schema(type = "string", format = "date")
        )
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate?
    ): ResponseEntity<List<MaintenanceHistoryResponseDTO>> {
        val filter = MaintenanceHistoryFilterDTO(status, startDate, endDate)
        val result = maintenanceHistoryService.findAllByVehicleId(vehicleId, page, size, filter)
        return ResponseEntity.ok(result)
    }


    @Operation(
        summary = "Create a new maintenance history for a vehicle",
        description = "Creates a new maintenance history record for a given vehicle. " +
                "If the maintenance date is not provided, the current date will be used by default."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Successfully created a new maintenance history record"),
            ApiResponse(responseCode = "400", description = "Bad request, possibly due to invalid input data")
        ]
    )
    @PostMapping
    fun createMaintenanceHistory(
        @PathVariable vehicleId: Long,
        @Valid @RequestBody maintenance: MaintenanceHistoryRequestDTO,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        @Schema(
            description = "If not provided, current date is used. eg.: 2025-04-06T14:30:00Z",
        ) date: LocalDateTime?
    ): ResponseEntity<MaintenanceHistoryResponseDTO> {
        val newMaintenance = maintenanceHistoryService.createMaintenanceHistory(vehicleId, maintenance, date)
        return ResponseEntity
            .created(URI.create("/api/v1/vehicles/${newMaintenance.id}"))
            .body(newMaintenance)
    }


    @PutMapping("/{id}")
    @Operation(
        summary = "Update a maintenance history for a vehicle",
        description = "If the maintenance date is not provided, the date will be set to current date and time."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "OK"),
            ApiResponse(responseCode = "404", description = "Not Found")
        ]
    )
    fun updateMaintenanceHistory(
        @PathVariable vehicleId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody maintenance: MaintenanceHistoryRequestDTO,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        @Schema(
            description = "If not provided, the date will be updated to current date. eg.: 2025-04-06T14:30:00Z",
        ) date: LocalDateTime?
    ): ResponseEntity<MaintenanceHistoryResponseDTO> {
        val updated = maintenanceHistoryService.updateMaintenanceHistory(vehicleId, id, maintenance, date)
        return ResponseEntity.ok(updated)
    }

    @Operation(summary = "Delete a maintenance history for a vehicle")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "No Content"),
            ApiResponse(responseCode = "404", description = "Not Found")
        ]
    )
    @DeleteMapping("/{id}")
    fun deleteMaintenanceHistory(
        @PathVariable vehicleId: Long,
        @PathVariable id: Long
    ): ResponseEntity<Unit> {
        maintenanceHistoryService.deleteMaintenanceHistory(vehicleId, id)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "Find maintenance history by ID for a specific vehicle")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "OK"),
            ApiResponse(responseCode = "404", description = "Not Found")
        ]
    )
    @GetMapping("/{id}")
    fun getMaintenanceHistoryById(
        @PathVariable vehicleId: Long,
        @PathVariable id: Long
    ): ResponseEntity<MaintenanceHistoryResponseDTO> {
        val maintenanceHistory = maintenanceHistoryService.findByVehicleIdAndId(vehicleId, id)
        return ResponseEntity.ok(maintenanceHistory)
    }
}