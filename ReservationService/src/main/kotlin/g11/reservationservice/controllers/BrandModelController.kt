package g11.reservationservice.controllers

import g11.reservationservice.entities.BrandModel
import g11.reservationservice.services.BrandModelService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "Brand Model", description = "Brand Model API")
@RestController
@RequestMapping("/api/v1/brandModels")
class BrandModelController(
    private val brandModelService: BrandModelService
) {

    @Operation(
        summary = "Get all models by brand",
        description = "Retrieve a list of all models for a specific brand. If not available, return an empty list."
    )
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping("/{brand}")
    fun getModelsByBrand(@PathVariable brand: String): ResponseEntity<List<String>> {
        val models = brandModelService.getModelsByBrand(brand)
        return ResponseEntity.ok(models)
    }

    @Operation(
        summary = "Get all brand models",
        description = "Retrieve a list of all available brand models."
    )
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping
    fun getAllBrandModels(): ResponseEntity<Map<String, List<String>>> {
        val result = brandModelService.getAllBrandModels()
        return ResponseEntity.ok(result)
    }
}