package g11.reservationservice.specifications

import g11.reservationservice.dtos.CarModelFilterDTO
import g11.reservationservice.entities.*
import g11.reservationservice.entities.enumerations.Category
import g11.reservationservice.entities.enumerations.Drivetrain
import g11.reservationservice.entities.enumerations.EngineType
import g11.reservationservice.entities.enumerations.TransmissionType
import org.springframework.data.jpa.domain.Specification
import jakarta.persistence.criteria.JoinType
import jakarta.persistence.criteria.Predicate


object CarModelSpecification {

    fun buildFilter(filter: CarModelFilterDTO): Specification<CarModel> {
        return Specification { root, query, cb ->

            val predicates = mutableListOf<Predicate>()

            // Brands & Models
            filter.brandModels?.let { brandModelList ->
                val brandModelMap = parseBrandModels(brandModelList)
                if (brandModelMap.isNotEmpty()) {
                    val brandModelPredicates = brandModelMap.map { (brand, models) ->
                        val brandPath = root.get<BrandModel>("brandModel")
                            .get<Brand>("brand")
                            .get<String>("name")
                        val brandPredicate = cb.equal(brandPath, brand)

                        if (models != null) {
                            val modelPath = root.get<BrandModel>("brandModel").get<String>("model")
                            val modelPredicate = modelPath.`in`(models)
                            cb.and(brandPredicate, modelPredicate)
                        } else {
                            brandPredicate
                        }
                    }
                    predicates.add(cb.or(*brandModelPredicates.toTypedArray()))
                }
            }

            // Features (OR)
            filter.features?.let {
                if (it.isNotEmpty()) {
                    val featureJoin = root.join<CarModel, Feature>("features", JoinType.LEFT)
                    val featurePredicate = featureJoin.get<String>("name").`in`(it)
                    predicates.add(featurePredicate)
                }
            }

            // Segments (OR)
            filter.segments?.let {
                if (it.isNotEmpty()) {
                    val segmentPath = root.get<Segment>("segment")
                    val segmentPredicate = segmentPath.`in`(it.map(::Segment))
                    predicates.add(segmentPredicate)
                }
            }

            // Rental price range
            filter.minRentalPrice?.let {
                val pricePath = root.get<Float>("rentalPricePerDay")
                predicates.add(cb.greaterThanOrEqualTo(pricePath, it))
            }
            filter.maxRentalPrice?.let {
                val pricePath = root.get<Float>("rentalPricePerDay")
                predicates.add(cb.lessThanOrEqualTo(pricePath, it))
            }

            // Additional fields (examples):
            filter.modelYears?.let {
                if (it.isNotEmpty()) {
                    val yearPath = root.get<Int>("modelYear")
                    predicates.add(yearPath.`in`(it))
                }
            }

            filter.categories?.let {
                if (it.isNotEmpty()) {
                    val catPath = root.get<Category>("category")
                    predicates.add(catPath.`in`(it))
                }
            }

            filter.engineTypes?.let {
                if (it.isNotEmpty()) {
                    val enginePath = root.get<EngineType>("engineType")
                    predicates.add(enginePath.`in`(it))
                }
            }

            filter.transmissionTypes?.let {
                if (it.isNotEmpty()) {
                    val transPath = root.get<TransmissionType>("transmissionType")
                    predicates.add(transPath.`in`(it))
                }
            }

            filter.drivetrains?.let {
                if (it.isNotEmpty()) {
                    val drivePath = root.get<Drivetrain>("drivetrain")
                    predicates.add(drivePath.`in`(it))
                }
            }

            cb.and(*predicates.toTypedArray())
        }
    }

    private fun parseBrandModels(brandModels: List<String>): Map<String, List<String>?> {
        return brandModels.associate { entry ->
            val split = entry.split(":").map { it.trim() }
            val brand = split[0]
            val models = if (split.size > 1) split[1].split("|").map { it.trim() } else null
            brand to models
        }
    }
}
