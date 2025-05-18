package g11.reservationservice.controllers


import g11.reservationservice.services.BrandService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Brand", description = "Brand API")
@RestController
@RequestMapping("/api/v1/brands")
class BrandController(
    private val brandService: BrandService
) {
    @Operation(
        summary = "Get all brands",
        description = "Retrieve a list of all available brands."
    )
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping
    fun getAllBrands(): ResponseEntity<List<String>> {
        val brands = brandService.getAllBrands()
        return ResponseEntity.ok(brands)
    }
}