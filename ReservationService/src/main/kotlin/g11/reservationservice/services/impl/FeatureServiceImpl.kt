package g11.reservationservice.services.impl

import g11.reservationservice.entities.Feature
import g11.reservationservice.repositories.FeatureRepository
import g11.reservationservice.services.FeatureService
import org.springframework.stereotype.Service

@Service
class FeatureServiceImpl(
    private val featureRepository: FeatureRepository
) : FeatureService {

    override fun getFeatureById(id: Long): Feature {
        if (id <= 0) {
            throw IllegalArgumentException("Invalid feature ID")
        }
        return featureRepository.findById(id).orElseThrow {
            throw NoSuchElementException("Feature with ID $id not found")
        }
    }


    override fun getAllFeatures(): List<String> {
        return featureRepository.findAll().map { it.name }
    }

    override fun addFeature(featureName: String): Feature {
        if (featureRepository.existsByName(featureName)) {
            throw IllegalArgumentException("Feature with name '$featureName' already exists.")
        }
        return featureRepository.save(Feature(name = featureName))
    }

    override fun findFeatureByName(featureName: String): Feature? {
        return featureRepository.findByName(featureName)
    }

    override fun findOrCreateFeature(featureName: String): Feature {
        return findFeatureByName(featureName) ?: addFeature(featureName)
    }
}