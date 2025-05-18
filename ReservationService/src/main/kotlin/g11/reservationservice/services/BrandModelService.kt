package g11.reservationservice.services

import g11.reservationservice.entities.Brand
import g11.reservationservice.entities.BrandModel

interface BrandModelService {
    fun getAllBrandModels(): Map<String, List<String>>
    fun addBrandModel(brandModel: BrandModel): BrandModel
    fun findByBrandAndModel(brand: String, model: String): BrandModel?
    fun getModelsByBrand(brand: String): List<String>
    fun findOrCreateBrandModel(brand: String, model: String): BrandModel
}
