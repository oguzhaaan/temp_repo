package g11.reservationservice.controllers

import g11.reservationservice.dtos.*
import g11.reservationservice.entities.enumerations.VehicleStatus
import g11.reservationservice.services.VehicleService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI


@Tag(name = "Vehicle", description = "Vehicles API")
@RestController
@RequestMapping("/api/v1/vehicles")
class VehicleController(private val vehicleService: VehicleService) {

    @Operation(summary = "Find all vehicles with optional filters and sorting")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "List of vehicles successfully retrieved")
        ]
    )
    @GetMapping
    fun getAllVehicles(
        @Parameter(description = "Page number (starts from 0)")
        @RequestParam(defaultValue = "0") page: Int,

        @Parameter(description = "Page size")
        @RequestParam(defaultValue = "10") size: Int,

        @Parameter(description = "Field to sort by. Allowed values: id, licensePlate, vin, kilometersTravelled. Otherwise id will be used as default.")
        @RequestParam(defaultValue = "id") sortBy: String,

        @Parameter(description = "Sort direction. Allowed values: asc, desc. Otherwise asc will be used as default.")
        @RequestParam(defaultValue = "asc") direction: String,

        @Parameter(description = "Filter by license plate (optional)")
        @RequestParam(required = false) licensePlate: String?,

        @Parameter(description = "Filter by VIN (optional)")
        @RequestParam(required = false) vin: String?,

        @Parameter(description = "Minimum kilometers travelled (optional)")
        @RequestParam(required = false) minKilometers: Int?,

        @Parameter(description = "Maximum kilometers travelled (optional)")
        @RequestParam(required = false) maxKilometers: Int?,

        @Parameter(description = "Filter by vehicle statuses (optional)")
        @RequestParam(required = false) statuses: List<VehicleStatus>?,

        @Parameter(description = "Filter by model IDs (optional)")
        @RequestParam(required = false) modelIds: List<Long>?
    ): ResponseEntity<List<VehicleResponseDTO>> {
        val filter = VehicleFilterDTO(
            licensePlate = licensePlate,
            vin = vin,
            minKilometers = minKilometers,
            maxKilometers = maxKilometers,
            statuses = statuses,
            modelIds = modelIds
        )
        return ResponseEntity.ok(
            vehicleService.getAllVehicles(page, size, sortBy, direction, filter)
        )
    }

    @Operation(summary = "Find a vehicle by Id")
    @ApiResponses(
        value = [ApiResponse(responseCode = "200", description = "OK"),
            ApiResponse(responseCode = "404", description = "Not Found")]
    )
    @GetMapping("/{id}")
    fun getVehicleById(@PathVariable id: Long): ResponseEntity<VehicleResponseDTO> {
        val vehicle = vehicleService.getVehicleById(id)
        return ResponseEntity.ok(vehicle)
    }

    @Operation(summary = "Create a new vehicle")
    @ApiResponses(
        value = [ApiResponse(responseCode = "201", description = "Created"),
            ApiResponse(responseCode = "400", description = "Bad Request")]
    )
    @PostMapping
    fun createVehicle(@Valid @RequestBody vehicle: VehicleRequestDTO): ResponseEntity<VehicleResponseDTO> {
        val newVehicle = vehicleService.addNewVehicle(vehicle)
        return ResponseEntity
            .created(URI.create("/vehicles/${newVehicle.id}"))
            .body(newVehicle)
    }

    @Operation(summary = "Update a vehicle by Id")
    @ApiResponses(
        value = [ApiResponse(responseCode = "200", description = "OK"),
            ApiResponse(responseCode = "404", description = "Not Found")]
    )
    @PutMapping("/{id}")
    fun updateVehicle(@PathVariable id: Long, @Valid @RequestBody vehicle: VehicleUpdateDTO):
            ResponseEntity<VehicleResponseDTO> {
        val newVehicle = vehicleService.updateVehicle(vehicle, id)
        return ResponseEntity.ok(newVehicle)
    }

    @Operation(summary = "Delete a vehicle by Id")
    @ApiResponses(
        value = [ApiResponse(responseCode = "204", description = "No Content"),
            ApiResponse(responseCode = "404", description = "Not Found")]
    )
    @DeleteMapping("/{id}")
    fun deleteVehicle(@PathVariable id: Long): ResponseEntity<Unit> {
        vehicleService.deleteVehicleById(id)
        return ResponseEntity.noContent().build()
    }
}