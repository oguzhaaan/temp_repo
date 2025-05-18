package g11.reservationservice.services.impl

import g11.reservationservice.dtos.*
import g11.reservationservice.entities.CarModel
import g11.reservationservice.exceptions.CarModelNotFoundException
import g11.reservationservice.exceptions.DuplicateModelException
import g11.reservationservice.mappers.toDto
import g11.reservationservice.mappers.toEntity
import g11.reservationservice.repositories.CarModelRepository
import g11.reservationservice.services.BrandModelService
import g11.reservationservice.services.CarModelService
import g11.reservationservice.services.FeatureService
import g11.reservationservice.services.SegmentService
import g11.reservationservice.specifications.CarModelSpecification
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CarModelServiceImpl(
    private val carModelRepository: CarModelRepository,
    private val brandModelService: BrandModelService,
    private val featureService: FeatureService,
    private val segmentService: SegmentService
) : CarModelService {

    private val logger = LoggerFactory.getLogger(CarModelServiceImpl::class.java)

    @Transactional(readOnly = true)
    override fun getAllCarModels(
        page: Int,
        size: Int,
        sortBy: String,
        direction: String,
        carModelFilterDTO: CarModelFilterDTO
    ): List<CarModelResponseDTO> {
        val allowedSortFields = listOf("id", "modelYear", "luggageCapacity", "rentalPricePerDay")

        // Validate sort field
        val sortField = if (sortBy in allowedSortFields) sortBy else "id"

        // Validate direction
        val sortDirection = when (direction.lowercase()) {
            "asc" -> Sort.Direction.ASC
            "desc" -> Sort.Direction.DESC
            else -> Sort.Direction.ASC // default fallback
        }

        val pageable: Pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField))

        val spec = CarModelSpecification.buildFilter(carModelFilterDTO)

        val carModels = carModelRepository.findAll(spec, pageable)

        return carModels.map { it.toDto() }.toList()
    }

    @Transactional(readOnly = true)
    override fun getCarModelById(id: Long): CarModelResponseDTO {
        val carModel =
            carModelRepository.findByIdOrNull(id) ?: throw CarModelNotFoundException(id)
        return carModel.toDto()
    }

    @Transactional
    override fun createCarModel(carModel: CarModelRequestDTO): CarModelResponseDTO {
        val features = carModel.features.map { featureService.findOrCreateFeature(it) }
        val brandModel = brandModelService.findOrCreateBrandModel(carModel.brand, carModel.model)
        val segment = segmentService.findOrCreateSegment(carModel.segment)
        val newCarModel = carModel.toEntity(features = features, segment = segment, brandModel = brandModel)
        if (isDuplicate(newCarModel)) {
            throw DuplicateModelException()
        }
        val savedCarModel = carModelRepository.save(newCarModel)
        logger.info("Created new car model: ${savedCarModel.getId()} - ${savedCarModel.brandModel.brand} ${savedCarModel.brandModel.model}")
        return savedCarModel.toDto()
    }

    @Transactional
    override fun updateCarModel(id: Long, updateDTO: CarModelUpdateDTO): CarModelResponseDTO {
        val existing = carModelRepository.findByIdOrNull(id)
            ?: throw CarModelNotFoundException(id)

        val before = existing.toDto()
        applyCarModelUpdates(existing, updateDTO)

        if (isDuplicate(existing)) {
            throw DuplicateModelException()
        }

        val saved = carModelRepository.save(existing)
        val after = saved.toDto()
        logger.info("Car model with ID $id updated. Before: $before | After: $after")

        return after
    }

    @Transactional
    override fun deleteCarModel(id: Long) {
        if (!carModelRepository.existsById(id)) {
            throw CarModelNotFoundException(id) // Custom exception thrown for 404
        }
        carModelRepository.deleteById(id)
        logger.info("Deleted car model with ID: $id")
    }

    @Transactional
    override fun findCarModelsWithAvailableVehicles(timeInterval: ReservationTimeIntervalDTO)
            : List<CarModelResponseDTO> {
        return carModelRepository.findAvailableCarModelsDuring(timeInterval.startDate, timeInterval.endDate)
            .map { it.toDto() }.toList()
    }

    private fun applyCarModelUpdates(carModel: CarModel, dto: CarModelUpdateDTO) {
        dto.brand?.let { brand ->
            dto.model?.let { model ->
                carModel.brandModel = brandModelService.findOrCreateBrandModel(brand, model)
            }
        }

        dto.modelYear?.let { carModel.modelYear = it }
        dto.segment?.let { carModel.segment = segmentService.findOrCreateSegment(it) }
        dto.numberOfDoors?.let { carModel.numberOfDoors = it }
        dto.seatingCapacity?.let { carModel.seatingCapacity = it }
        dto.luggageCapacity?.let { carModel.luggageCapacity = it }
        dto.category?.let { carModel.category = it }
        dto.engineType?.let { carModel.engineType = it }
        dto.transmissionType?.let { carModel.transmissionType = it }
        dto.drivetrain?.let { carModel.drivetrain = it }
        dto.motorDisplacement?.let { carModel.motorDisplacement = it }
        dto.features?.let { featureNames ->
            carModel.features.run {
                clear()
                addAll(featureNames.map { featureService.findOrCreateFeature(it) })
            }
        }
        dto.rentalPricePerDay?.let { carModel.rentalPricePerDay = it }
    }

    private fun isDuplicate(carModel: CarModel): Boolean {
        val existingModels = carModelRepository.findByBrandModelAndModelYear(
            carModel.brandModel,
            carModel.modelYear
        )

        val newFeatureIds = carModel.features.map { it.getId() }.toSet()

        return existingModels.any { existing ->
            existing.getId() != carModel.getId() &&
                    existing.segment == carModel.segment &&
                    existing.numberOfDoors == carModel.numberOfDoors &&
                    existing.seatingCapacity == carModel.seatingCapacity &&
                    existing.luggageCapacity == carModel.luggageCapacity &&
                    existing.category == carModel.category &&
                    existing.engineType == carModel.engineType &&
                    existing.transmissionType == carModel.transmissionType &&
                    existing.drivetrain == carModel.drivetrain &&
                    existing.motorDisplacement == carModel.motorDisplacement &&
                    existing.rentalPricePerDay == carModel.rentalPricePerDay &&
                    existing.features.map { it.getId() }.toSet() == newFeatureIds
        }
    }

}