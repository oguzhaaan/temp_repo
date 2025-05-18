package g11.reservationservice.services.impl

import g11.reservationservice.dtos.MaintenanceHistoryFilterDTO
import g11.reservationservice.dtos.MaintenanceHistoryRequestDTO
import g11.reservationservice.dtos.MaintenanceHistoryResponseDTO
import g11.reservationservice.dtos.ReservationTimeIntervalDTO
import g11.reservationservice.entities.MaintenanceHistory
import g11.reservationservice.entities.Vehicle
import g11.reservationservice.entities.enumerations.MaintenanceStatus
import g11.reservationservice.exceptions.MaintenanceNotBelongsToVehicleException
import g11.reservationservice.exceptions.MaintenanceNotFoundException
import g11.reservationservice.exceptions.VehicleNotFoundException
import g11.reservationservice.mappers.toEntity
import g11.reservationservice.mappers.toResponseDTO
import g11.reservationservice.repositories.MaintenanceHistoryRepository
import g11.reservationservice.repositories.NoteRepository
import g11.reservationservice.repositories.VehicleRepository
import g11.reservationservice.services.MaintenanceHistoryService
import g11.reservationservice.specifications.MaintenanceHistorySpecification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import kotlin.NoSuchElementException
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class MaintenanceHistoryImpl(
    private val maintenanceHistoryRepository: MaintenanceHistoryRepository,
    private val vehicleRepository: VehicleRepository,
    private val noteRepository: NoteRepository
) : MaintenanceHistoryService {

    private val logger = LoggerFactory.getLogger(CarModelServiceImpl::class.java)


    @Transactional
    override fun createMaintenanceHistory(
        vehicleId: Long,
        maintenanceHistory: MaintenanceHistoryRequestDTO,
        date: LocalDateTime?
    ): MaintenanceHistoryResponseDTO {

        // check if vehicle exist
        val vehicle = vehicleRepository.findByIdOrNull(vehicleId)
            ?: throw NoSuchElementException("No vehicle with corresponding id found")

        // check if date is null
        // if null use current date
        // if not null use the date provided
        // if not null, check if date is in the future
        // if so, check if status is UPCOMING - if not, throw exception
        val currentDate = LocalDateTime.now()
        val maintenanceDate = date ?: currentDate

        if (maintenanceDate.isAfter(currentDate) && maintenanceHistory.maintenanceStatus != MaintenanceStatus.UPCOMING) {
            throw IllegalArgumentException("Maintenance date cannot be in the future unless status is UPCOMING")
        }

        val maintenanceHistory = maintenanceHistory.toEntity(vehicle, maintenanceDate)
        val newHistory = maintenanceHistoryRepository.save(maintenanceHistory)
        val result = newHistory.toResponseDTO(vehicleId)

        // log
        logger.info(
            "Maintenance history with ID ${result.id} created. Vehicle ID: $vehicleId | Maintenance History: $result"
        )
        return result
    }

    @Transactional
    override fun updateMaintenanceHistory(
        vehicleId: Long,
        id: Long,
        newMaintenanceHistory: MaintenanceHistoryRequestDTO,
        date: LocalDateTime?
    ): MaintenanceHistoryResponseDTO {
        // check if vehicle exists
        vehicleRepository.findByIdOrNull(vehicleId)
            ?: throw VehicleNotFoundException(vehicleId)

        // check if maintenance history exists
        val existingHistory =
            maintenanceHistoryRepository.findByIdOrNull(id)
                ?: throw MaintenanceNotFoundException(id)

        // check if history belongs to the vehicle
        if (existingHistory.vehicle.getId() != vehicleId) {
            throw MaintenanceNotBelongsToVehicleException(vehicleId, id)
        }

        // check if date is null
        // if null use current date
        // if not null use the date provided
        // if not null, check if date is in the future
        // if so, check if status is UPCOMING - if not, throw exception
        val currentDate = LocalDateTime.now()
        val maintenanceDate = date ?: currentDate

        if (maintenanceDate.isAfter(currentDate) && newMaintenanceHistory.maintenanceStatus != MaintenanceStatus.UPCOMING) {
            throw IllegalArgumentException("Maintenance date cannot be in the future unless status is UPCOMING")
        }

        updateFromDTO(existingHistory, newMaintenanceHistory)

        existingHistory.maintenanceDate = maintenanceDate

        // save the updated history
        val updatedHistory = maintenanceHistoryRepository.save(existingHistory).toResponseDTO(vehicleId)

        logger.info(
            "Maintenance history with ID $id updated. Before: $existingHistory | After: $updatedHistory"
        )

        return updatedHistory
    }

    @Transactional(readOnly = true)
    override fun findAllByVehicleId(
        vehicleId: Long,
        page: Int,
        size: Int,
        filter: MaintenanceHistoryFilterDTO
    ): List<MaintenanceHistoryResponseDTO> {
        vehicleRepository.findByIdOrNull(vehicleId)
            ?: throw VehicleNotFoundException(vehicleId)

        val pageable = PageRequest.of(page, size)
        val spec = Specification.where(MaintenanceHistorySpecification.fromFilter(filter))
            .and(Specification { root, _, cb ->
                cb.equal(root.get<Vehicle>("vehicle").get<Long>("id"), vehicleId)
            })

        return maintenanceHistoryRepository.findAll(spec, pageable)
            .map { it.toResponseDTO(vehicleId) }
            .toList()
    }

    @Transactional(readOnly = true)
    override fun findByVehicleIdAndId(vehicleId: Long, id: Long): MaintenanceHistoryResponseDTO {
        // check if vehicle exists
        vehicleRepository.findByIdOrNull(vehicleId)
            ?: throw VehicleNotFoundException(vehicleId)

        // check if history exists and belongs to the vehicle
        val history = maintenanceHistoryRepository.findByIdOrNull(id)
            ?: throw MaintenanceNotFoundException(id)

        // if the history does not belong to the vehicle, throw an exception
        if (history.vehicle.getId() != vehicleId) {
            throw MaintenanceNotBelongsToVehicleException(vehicleId, id)
        }

        // Return history as a response DTO
        return history.toResponseDTO(vehicleId)
    }

    @Transactional
    override fun deleteMaintenanceHistory(vehicleId: Long, id: Long) {
        // check if vehicle exists
        vehicleRepository.findByIdOrNull(vehicleId)
            ?: throw VehicleNotFoundException(vehicleId)

        // check if history exists and belongs to the vehicle
        val history = maintenanceHistoryRepository.findByIdOrNull(id)
            ?: throw MaintenanceNotFoundException(id)

        // if the history does not belong to the vehicle, throw an exception
        if (history.vehicle.getId() != vehicleId) {
            throw MaintenanceNotBelongsToVehicleException(vehicleId, id)
        }

        // delete the history
        maintenanceHistoryRepository.delete(history)

        // log
        logger.info(
            "Maintenance history with ID $id deleted. Vehicle ID: $vehicleId | Maintenance History: ${history.toResponseDTO(vehicleId)}"
        )
    }

    override fun ifMaintenanceExists(
        vehicleId: Long,
        status: MaintenanceStatus,
        timeIntervalDTO: ReservationTimeIntervalDTO
    ): Boolean {
        return maintenanceHistoryRepository.existsByVehicleIdAndMaintenanceStatusAndMaintenanceDateBetween(
            vehicleId,
            status,
            timeIntervalDTO.startDate,
            timeIntervalDTO.endDate
        )
    }

    private fun updateFromDTO(
        existingHistory: MaintenanceHistory,
        newHistoryDTO: MaintenanceHistoryRequestDTO
    ) {
        existingHistory.defect = newHistoryDTO.defect
        existingHistory.maintenanceStatus = newHistoryDTO.maintenanceStatus
        existingHistory.service = newHistoryDTO.service
        existingHistory.maintenanceDescription = newHistoryDTO.maintenanceDescription
        existingHistory.maintenanceDate = LocalDateTime.now()
    }

}