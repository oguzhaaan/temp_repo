package g11.reservationservice.repositories

import g11.reservationservice.entities.Feature
import org.springframework.data.jpa.repository.JpaRepository

interface FeatureRepository: JpaRepository<Feature, Long> {
    fun findByName(featureName: String): Feature?
    fun existsByName(featureName: String): Boolean
}