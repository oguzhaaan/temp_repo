package g11.reservationservice.controllers

import g11.reservationservice.dtos.NoteFilterDTO
import g11.reservationservice.dtos.NoteRequestDTO
import g11.reservationservice.dtos.NoteResponseDTO
import g11.reservationservice.services.NoteService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.time.LocalDate


@Tag(name = "Note", description = "Notes API")
@RestController
@RequestMapping("/api/v1/vehicles/{vehicleId}/notes")
class NoteController(private val noteService: NoteService) {

    @Operation(summary = "Find notes for a specific vehicle with optional filters")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "List of notes retrieved"),
            ApiResponse(responseCode = "400", description = "Invalid request parameters (e.g., incorrect date format)"),
            ApiResponse(responseCode = "404", description = "Vehicle not found")
        ]
    )
    @GetMapping("/")
    fun getNoteByVehicleId(
        @PathVariable vehicleId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) author: String?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate?
    ): ResponseEntity<List<NoteResponseDTO>> {
        val filter = NoteFilterDTO(author, startDate, endDate)
        val notes = noteService.findAllByVehicleId(vehicleId, page, size, filter)
        return ResponseEntity.ok(notes)
    }


    @Operation(summary = "Add note for a specific vehicle")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Created"),
            ApiResponse(responseCode = "400", description = "Bad Request"),
        ]
    )
    @PostMapping
    fun createNote(
        @PathVariable vehicleId: Long,
        @Valid @RequestBody noteRequest: NoteRequestDTO
    ): ResponseEntity<NoteResponseDTO> {
        val newNote = noteService.addNoteByVehicleId(vehicleId, noteRequest)
        return ResponseEntity.
            created(URI.create("/api/v1/vehicles/$vehicleId/notes/${newNote.id}")).
            body(newNote)
    }

    @Operation(summary = "Update note for a specific vehicle")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "OK"),
            ApiResponse(responseCode = "400", description = "Bad Request"),
            ApiResponse(responseCode = "404", description = "Note not found")
        ]
    )
    @PutMapping("/{id}")
    fun updateNote(@PathVariable vehicleId: Long, @PathVariable id: Long, @Valid @RequestBody note: NoteRequestDTO):
            ResponseEntity<NoteResponseDTO> {
        val newNote = noteService.updateNote(vehicleId, id, note)
        return ResponseEntity.ok(newNote)
    }

    @Operation(summary = "Delete note for a specific vehicle")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "No Content"),
            ApiResponse(responseCode = "404", description = "Note not found")
        ]
    )
    @DeleteMapping("/{id}")
    fun deleteNote(@PathVariable vehicleId: Long, @PathVariable id: Long): ResponseEntity<Unit> {
        noteService.deleteNote(vehicleId, id)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "Get note by id and vehicle id")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "OK"),
            ApiResponse(responseCode = "404", description = "Note not found")
        ]
    )
    @GetMapping("/{id}")
    fun getNoteById(@PathVariable vehicleId: Long, @PathVariable id: Long): ResponseEntity<NoteResponseDTO> {
        val note = noteService.findByVehicleIdAndId(vehicleId, id)
        return ResponseEntity.ok(note)
    }
}