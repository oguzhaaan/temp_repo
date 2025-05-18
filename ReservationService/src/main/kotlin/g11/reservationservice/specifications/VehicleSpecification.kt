package g11.reservationservice.specifications

import g11.reservationservice.dtos.VehicleFilterDTO
import g11.reservationservice.entities.CarModel
import g11.reservationservice.entities.Vehicle
import g11.reservationservice.entities.enumerations.VehicleStatus
import org.springframework.data.jpa.domain.Specification
import jakarta.persistence.criteria.Predicate


object VehicleSpecification {

    fun fromFilter(filter: VehicleFilterDTO): Specification<Vehicle> {
        return Specification.where<Vehicle>(null)
            .and(licensePlateLike(filter.licensePlate))
            .and(vinLike(filter.vin))
            .and(kilometerRange(filter.minKilometers, filter.maxKilometers))
            .and(statusIn(filter.statuses))
            .and(modelIdIn(filter.modelIds))
    }


    private fun licensePlateLike(plate: String?): Specification<Vehicle>? {
        if (plate.isNullOrBlank()) return null
        return Specification { root, _, cb ->
            cb.like(cb.lower(root.get("licensePlate")), "%${plate.lowercase()}%")
        }
    }

    private fun vinLike(vin: String?): Specification<Vehicle>? {
        if (vin.isNullOrBlank()) return null
        return Specification { root, _, cb ->
            cb.like(cb.lower(root.get("vin")), "%${vin.lowercase()}%")
        }
    }

    private fun kilometerRange(min: Int?, max: Int?): Specification<Vehicle>? {
        return Specification { root, _, cb ->
            val predicates = mutableListOf<Predicate>()
            min?.let { predicates.add(cb.greaterThanOrEqualTo(root.get("kilometersTravelled"), it)) }
            max?.let { predicates.add(cb.lessThanOrEqualTo(root.get("kilometersTravelled"), it)) }
            cb.and(*predicates.toTypedArray())
        }
    }

    private fun statusIn(statuses: List<VehicleStatus>?): Specification<Vehicle>? {
        if (statuses.isNullOrEmpty()) return null
        return Specification { root, _, cb ->
            root.get<VehicleStatus>("vehicleStatus").`in`(statuses)
        }
    }

    private fun modelIdIn(modelIds: List<Long>?): Specification<Vehicle>? {
        if (modelIds.isNullOrEmpty()) return null
        return Specification { root, _, cb ->
            root.get<CarModel>("carModel").get<Long>("id").`in`(modelIds)
        }
    }
}

