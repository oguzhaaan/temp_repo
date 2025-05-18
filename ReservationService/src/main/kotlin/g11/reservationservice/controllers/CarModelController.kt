package g11.reservationservice.controllers

import g11.reservationservice.dtos.*
import g11.reservationservice.entities.enumerations.Category
import g11.reservationservice.entities.enumerations.Drivetrain
import g11.reservationservice.entities.enumerations.EngineType
import g11.reservationservice.entities.enumerations.TransmissionType
import g11.reservationservice.services.CarModelService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.time.LocalDate


@Tag(name = "Car Model", description = "Car Model API")
@RestController
@RequestMapping("/api/v1/models")
class CarModelController(private val carModelService: CarModelService) {

    @Operation(
        summary = "Get all car models",
        description = "Get all car models with optional pagination and filtering by brand & model."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "OK"),
            ApiResponse(responseCode = "400", description = "Bad Request"),
        ]
    )
    @GetMapping
    fun getAllCarModels(
        @Parameter(description = "Page number (starts from 0).")
        @RequestParam(defaultValue = "0") page: Int,

        @Parameter(description = "Page size (number of items per page).")
        @RequestParam(defaultValue = "10") size: Int,

        @Parameter(description = "Field to sort by. Allowed values: id, modelYear, luggageCapacity, rentalPricePerDay. Otherwise, id will be used as the default.")
        @RequestParam(defaultValue = "id") sortBy: String,

        @Parameter(description = "Sort direction. Allowed values: asc, desc. Otherwise, asc will be used as the default.")
        @RequestParam(defaultValue = "asc") direction: String,

        @Parameter(
            description = "Brand and model filter. The parameter can be provided in the format: 'brandName' or 'brandName: model1 | model2'. e.g.: 'Toyota' or 'Toyota: Corolla | Camry'.",
        )
        @RequestParam brandModels: List<String>?,

        @Parameter(description = "List of car features to filter by.")
        @RequestParam features: List<String>?,

        @Parameter(description = "List of car segments (e.g., SUV, Sedan) to filter by.")
        @RequestParam segments: List<String>?,

        @Parameter(description = "Minimum rental price for filtering.")
        @RequestParam minRentalPrice: Float?,

        @Parameter(description = "Maximum rental price for filtering.")
        @RequestParam maxRentalPrice: Float?,

        @Parameter(description = "List of model years to filter by.")
        @RequestParam modelYears: List<Int>?,

        @Parameter(description = "List of car categories to filter by.")
        @RequestParam categories: List<Category>?,

        @Parameter(description = "List of engine types to filter by.")
        @RequestParam engineTypes: List<EngineType>?,

        @Parameter(description = "List of transmission types to filter by.")
        @RequestParam transmissionTypes: List<TransmissionType>?,

        @Parameter(description = "List of drivetrains to filter by.")
        @RequestParam drivetrains: List<Drivetrain>?
    ): ResponseEntity<List<CarModelResponseDTO>> {

        val carModelFilterDTO = CarModelFilterDTO(
            brandModels = brandModels,
            features = features,
            segments = segments,
            minRentalPrice = minRentalPrice,
            maxRentalPrice = maxRentalPrice,
            modelYears = modelYears,
            categories = categories,
            engineTypes = engineTypes,
            transmissionTypes = transmissionTypes,
            drivetrains = drivetrains
        )

        val carModels = carModelService.getAllCarModels(page, size, sortBy, direction, carModelFilterDTO)
        return ResponseEntity.ok(carModels)
    }


    @Operation(summary = "Get car models by ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "OK"),
            ApiResponse(responseCode = "400", description = "Bad Request"),
            ApiResponse(responseCode = "404", description = "Not Found"),
        ]
    )
    @GetMapping("/{id}")
    fun getCarModelById(@PathVariable id: Long): ResponseEntity<CarModelResponseDTO> {
        val carModel = carModelService.getCarModelById(id)
        return ResponseEntity.ok(carModel)
    }

    @Operation(summary = "Create a new car model")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Created"),
            ApiResponse(responseCode = "400", description = "Bad Request"),
        ]
    )
    @PostMapping
    fun createCarModel(@Valid @RequestBody carModel: CarModelRequestDTO): ResponseEntity<CarModelResponseDTO> {
        val newCarModel = carModelService.createCarModel(carModel)
        return ResponseEntity
            .created(URI.create("/api/v1/models/${newCarModel.id}"))
            .body(newCarModel)
    }

    @Operation(summary = "Update an existing car model")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "OK"),
            ApiResponse(responseCode = "400", description = "Bad Request"),
            ApiResponse(responseCode = "404", description = "Not Found"),
        ]
    )
    @PutMapping("/{id}")
    fun updateCarModel(
        @PathVariable id: Long,
        @Valid @RequestBody carModel: CarModelUpdateDTO
    ): ResponseEntity<CarModelResponseDTO> {
        val updatedCarModel = carModelService.updateCarModel(id, carModel)
        return ResponseEntity.ok(updatedCarModel)
    }

    @Operation(summary = "Delete a car model")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Successfully deleted the car model"),
            ApiResponse(responseCode = "404", description = "Car model with the given ID not found"),
        ]
    )
    @DeleteMapping("/{id}")
    fun deleteCarModel(@PathVariable id: Long): ResponseEntity<Unit> {
        carModelService.deleteCarModel(id)
        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "Find available car models",
        description = "Returns a list of car models that have at least one vehicle available for the given time interval.",
        responses = [
            ApiResponse(responseCode = "200", description = "List of available car models"),
            ApiResponse(responseCode = "400", description = "Invalid date input")
        ]
    )
    @GetMapping("/available")
    fun findAvailableCarModels(
        @RequestParam
        @Parameter(description = "Start date of reservation period - Future date")
        startDate: LocalDate,

        @RequestParam
        @Parameter(description = "End date of reservation period - Future date after start date")
        endDate: LocalDate
    ): List<CarModelResponseDTO> {
        val timeInterval = ReservationTimeIntervalDTO(startDate, endDate)
        return carModelService.findCarModelsWithAvailableVehicles(timeInterval)
    }

}
