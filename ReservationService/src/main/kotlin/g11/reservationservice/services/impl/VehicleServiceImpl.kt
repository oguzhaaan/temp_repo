package g11.reservationservice.services.impl

import g11.reservationservice.dtos.*
import g11.reservationservice.entities.Vehicle
import g11.reservationservice.exceptions.*
import g11.reservationservice.mappers.toEntity
import g11.reservationservice.mappers.toResponseDTO
import g11.reservationservice.repositories.VehicleRepository
import org.springframework.stereotype.Service
import g11.reservationservice.services.VehicleService
import org.springframework.data.repository.findByIdOrNull
import g11.reservationservice.repositories.CarModelRepository
import g11.reservationservice.specifications.VehicleSpecification
import org.springframework.data.domain.Sort
import org.springframework.data.domain.PageRequest
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

@Service
class VehicleServiceImpl(
    private val vehicleRepository: VehicleRepository,
    private val carModelRepository: CarModelRepository
) : VehicleService {

    private val logger = LoggerFactory.getLogger(CarModelServiceImpl::class.java)

    @Transactional(readOnly = true)
    override fun getVehicleById(id: Long): VehicleResponseDTO {
        val vehicle = vehicleRepository.findByIdOrNull(id) ?: throw VehicleNotFoundException(id)
        return vehicle.toResponseDTO()
    }

    @Transactional
    override fun addNewVehicle(vehicleDTO: VehicleRequestDTO): VehicleResponseDTO {
        val carModel = carModelRepository.findByIdOrNull(vehicleDTO.carModelId)
            ?: throw CarModelNotFoundException(vehicleDTO.carModelId)
        if (vehicleRepository.existsByLicensePlate(vehicleDTO.licensePlate)) {
            throw DuplicateLicensePlateException(vehicleDTO.licensePlate)
        }
        if (vehicleRepository.existsByVin(vehicleDTO.vin)) {
            throw DuplicateVinException(vehicleDTO.vin)
        }
        val vehicle = vehicleDTO.toEntity(carModel)
        val savedVehicle = vehicleRepository.save(vehicle)
        logger.info("New vehicle added: ${savedVehicle.getId()}, licensePlate=${savedVehicle.licensePlate}")
        return savedVehicle.toResponseDTO()
    }

    @Transactional
    override fun updateVehicle(updateDTO: VehicleUpdateDTO, id: Long): VehicleResponseDTO {
        val existingVehicle = vehicleRepository.findByIdOrNull(id)
            ?: throw VehicleNotFoundException(id)

        updateDTO.carModelId?.let {
            val carModel = carModelRepository.findByIdOrNull(it)
                ?: throw CarModelNotFoundException(it)
            existingVehicle.carModel = carModel
        }

        updateDTO.licensePlate?.let {
            // Check if another vehicle with the same license plate exists, but exclude the current vehicle
            if (vehicleRepository.existsByLicensePlateAndIdNot(it, id)) {
                throw DuplicateLicensePlateException(it)
            }
            existingVehicle.licensePlate = it
        }

        updateDTO.vin?.let {
            // Check if another vehicle with the same VIN exists, but exclude the current vehicle
            if (vehicleRepository.existsByVinAndIdNot(it, id)) {
                throw DuplicateVinException(it)
            }
            existingVehicle.vin = it
        }

        updateDTO.kilometersTravelled?.let {
            existingVehicle.kilometersTravelled = it
        }

        updateDTO.vehicleStatus?.let {
            existingVehicle.vehicleStatus = it
        }

        val savedVehicle = vehicleRepository.save(existingVehicle)
        logger.info("Vehicle updated: id=$id")
        return savedVehicle.toResponseDTO()
    }

    override fun getAllVehicles(
        page: Int,
        size: Int,
        sortBy: String,
        direction: String,
        filter: VehicleFilterDTO
    ): List<VehicleResponseDTO> {

        val allowedSortFields = listOf("id", "licensePlate", "vin", "kilometersTravelled")

        // Validate sort field
        val sortField = if (sortBy in allowedSortFields) sortBy else "id"

        // Validate direction
        val sortDirection = when (direction.lowercase()) {
            "asc" -> Sort.Direction.ASC
            "desc" -> Sort.Direction.DESC
            else -> Sort.Direction.ASC // default fallback
        }

        val pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField))
        val spec = VehicleSpecification.fromFilter(filter)

        return vehicleRepository.findAll(spec, pageable)
            .map { it.toResponseDTO() }
            .toList()
    }

    @Transactional
    override fun deleteVehicleById(id: Long) {
        vehicleRepository.findByIdOrNull(id) ?: throw VehicleNotFoundException(id)
        return vehicleRepository.deleteById(id)
        logger.info("Vehicle deleted: id=$id")
    }

    @Transactional
    override fun findAvailableVehicleForReservation(
        carModelId: Long,
        timeInterval: ReservationTimeIntervalDTO
    ): Vehicle {
        val availableVehicles = vehicleRepository.findFirstAvailableVehicleForReservation(
            carModelId,
            timeInterval.startDate,
            timeInterval.endDate
        )
        return availableVehicles.firstOrNull()
            ?: throw NoAvailableVehicleException("No available vehicle...")
    }

    override fun getVehicleInstanceById(vehicleId: Long): Vehicle {
        return vehicleRepository.findByIdOrNull(vehicleId) ?: throw VehicleNotFoundException(vehicleId)
    }

    @Transactional
    override fun deleteAllVehicles() {
        vehicleRepository.deleteAll()
        logger.info("Deleted all vehicles")
    }

}