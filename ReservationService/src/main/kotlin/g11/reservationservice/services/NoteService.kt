package g11.reservationservice.services

import g11.reservationservice.dtos.NoteFilterDTO
import g11.reservationservice.dtos.NoteRequestDTO
import g11.reservationservice.dtos.NoteResponseDTO

interface NoteService {
    fun findAllByVehicleId(vehicleId: Long, page: Int, size: Int, filter: NoteFilterDTO)
            : List<NoteResponseDTO>
    fun addNoteByVehicleId(vehicleId: Long, noteRequest: NoteRequestDTO): NoteResponseDTO
    fun updateNote(vehicleId: Long, id: Long, note: NoteRequestDTO): NoteResponseDTO
    fun deleteNote(vehicleId: Long, id: Long)
    fun findByVehicleIdAndId(vehicleId: Long, id: Long): NoteResponseDTO
}