package g11.reservationservice.controllers

import g11.reservationservice.entities.Segment
import g11.reservationservice.services.SegmentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Segment", description = "Segment API")
@RestController
@RequestMapping("/api/v1/segments")
class SegmentController(private val segmentService: SegmentService) {

    @Operation(
        summary = "Get all segments",
        description = "Retrieve a list of all available segments."
    )
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping
    fun getAllSegments(): ResponseEntity<List<String>> {
        val segments = segmentService.getAllSegments()
        return ResponseEntity.ok(segments)
    }
}