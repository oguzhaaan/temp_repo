package g11.reservationservice.controllers

import g11.reservationservice.entities.Feature
import g11.reservationservice.services.FeatureService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Feature", description = "Feature API")
@RestController
@RequestMapping("/api/v1/features")
class FeatureController(
    private val featureService: FeatureService
) {

    @Operation(summary = "Get all available features.",
        description = "Retrieve a list of all available features."
    )
    @GetMapping
    fun getAllFeatures(): ResponseEntity<List<String>> {
        val features = featureService.getAllFeatures()
        return ResponseEntity.ok(features)
    }
}
