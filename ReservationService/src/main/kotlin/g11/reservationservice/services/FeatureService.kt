package g11.reservationservice.services

import g11.reservationservice.entities.Feature

interface FeatureService {
    fun getFeatureById(id: Long): Feature
    fun findFeatureByName(featureName: String): Feature?
    fun getAllFeatures(): List<String>
    fun addFeature(featureName: String): Feature
    fun findOrCreateFeature(featureName: String): Feature
}