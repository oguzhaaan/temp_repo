package g11.reservationservice.mappers

import g11.reservationservice.dtos.NoteRequestDTO
import g11.reservationservice.dtos.NoteResponseDTO
import g11.reservationservice.entities.Note
import g11.reservationservice.entities.Vehicle
import java.time.LocalDateTime

fun Note.toResponseDTO(vehicleId: Long): NoteResponseDTO =
    NoteResponseDTO(
        id = this.getId(),
        vehicleId = vehicleId,
        note = this.note,
        date = this.date,
        author = this.author,
        type = this.type
    )

fun NoteRequestDTO.toEntity(vehicle: Vehicle, date: LocalDateTime): Note =
    Note(
        vehicle = vehicle,
        note = this.note,
        date = date,
        author = this.author,
        type = this.type
    )
