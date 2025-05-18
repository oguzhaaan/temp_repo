package g11.reservationservice.services.impl

import g11.reservationservice.dtos.NoteFilterDTO
import g11.reservationservice.dtos.NoteRequestDTO
import g11.reservationservice.dtos.NoteResponseDTO
import g11.reservationservice.entities.Vehicle
import g11.reservationservice.exceptions.NoteNotBelongsToVehicleException
import g11.reservationservice.exceptions.NoteNotFoundException
import g11.reservationservice.exceptions.VehicleNotFoundException
import g11.reservationservice.mappers.toEntity
import g11.reservationservice.mappers.toResponseDTO
import g11.reservationservice.repositories.NoteRepository
import g11.reservationservice.repositories.VehicleRepository
import g11.reservationservice.services.NoteService
import g11.reservationservice.specifications.NoteSpecification
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*


@Service
class NoteServiceImpl(
    private val noteRepository: NoteRepository,
    private val vehicleRepository: VehicleRepository
) : NoteService {

    private val logger = LoggerFactory.getLogger(CarModelServiceImpl::class.java)

    @Transactional(readOnly = true)
    override fun findAllByVehicleId(
        vehicleId: Long,
        page: Int,
        size: Int,
        filter: NoteFilterDTO
    ): List<NoteResponseDTO> {
        val vehicle = vehicleRepository.findByIdOrNull(vehicleId)
            ?: throw VehicleNotFoundException(vehicleId)

        val pageable = PageRequest.of(page, size)
        val spec = Specification.where(NoteSpecification.fromFilter(filter))
            .and(Specification { root, _, cb ->
                cb.equal(root.get<Vehicle>("vehicle").get<Long>("id"), vehicleId)
            })

        return noteRepository.findAll(spec, pageable)
            .map { it.toResponseDTO(vehicleId) }
            .toList()
    }


    @Transactional
    override fun addNoteByVehicleId(vehicleId: Long, noteRequest: NoteRequestDTO): NoteResponseDTO {
        val vehicle = vehicleRepository.findByIdOrNull(vehicleId)
            ?: throw VehicleNotFoundException(vehicleId)
        val note = noteRequest.toEntity(vehicle, LocalDateTime.now())
        val newNote = noteRepository.save(note)
        val result = newNote.toResponseDTO(vehicleId)
        // log
        logger.info(
            "Note with id ${result.id} for vehicle with id $vehicleId created successfully. Note: ${result.note}"
        )
        return result
    }

    @Transactional
    override fun updateNote(vehicleId: Long, id: Long, noteRequest: NoteRequestDTO): NoteResponseDTO {
        vehicleRepository.findByIdOrNull(vehicleId)
            ?: throw VehicleNotFoundException(vehicleId)
        val noteToUpdate = noteRepository.findByIdOrNull(id)
            ?: throw NoteNotFoundException(id)
        if (noteToUpdate.vehicle.getId() != vehicleId) {
            throw NoteNotBelongsToVehicleException(vehicleId, id)
        }
        noteToUpdate.note = noteRequest.note
        noteToUpdate.author = noteRequest.author
        noteToUpdate.type = noteRequest.type
        val updatedNote = noteRepository.save(noteToUpdate)

        val result = updatedNote.toResponseDTO(vehicleId)

        logger.info(
            "Note with id $id for vehicle with id $vehicleId updated successfully. Note: ${result.note}"
        )

        return result
    }


    @Transactional
    override fun deleteNote(vehicleId: Long, id: Long) {
        vehicleRepository.findByIdOrNull(vehicleId)
            ?: throw VehicleNotFoundException(vehicleId)
        val note = noteRepository.findByIdOrNull(id) ?: throw NoSuchElementException("No note found for $id")
        if (note.vehicle.getId() != vehicleId) {
            throw NoteNotBelongsToVehicleException(vehicleId, id)
        }
        noteRepository.delete(note)
        // log
        logger.info(
            "Note with id $id for vehicle with id $vehicleId deleted successfully."
        )
    }

    @Transactional(readOnly = true)
    override fun findByVehicleIdAndId(
        vehicleId: Long,
        id: Long
    ): NoteResponseDTO {
        vehicleRepository.findByIdOrNull(vehicleId)
            ?: throw VehicleNotFoundException(vehicleId)
        val note = noteRepository.findByIdOrNull(id) ?: throw NoteNotFoundException(id)
        if (note.vehicle.getId() != vehicleId) {
            throw NoteNotBelongsToVehicleException(vehicleId, id)
        }
        return note.toResponseDTO(vehicleId)
    }
}