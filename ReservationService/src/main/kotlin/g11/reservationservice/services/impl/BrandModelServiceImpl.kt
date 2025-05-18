package g11.reservationservice.services.impl

import g11.reservationservice.entities.Brand
import g11.reservationservice.entities.BrandModel
import g11.reservationservice.repositories.BrandModelRepository
import g11.reservationservice.services.BrandModelService
import g11.reservationservice.services.BrandService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class BrandModelServiceImpl(
    private val brandModelRepository: BrandModelRepository,
    private val brandService: BrandService
): BrandModelService {
    private val logger = LoggerFactory.getLogger(BrandModelServiceImpl::class.java)

    override fun getAllBrandModels(): Map<String, List<String>> {
        return brandModelRepository.findAll().groupBy { it.brand.name }  // Group by brand
            .mapValues { entry -> entry.value.map { it.model } }
    }

    override fun addBrandModel(brandModel: BrandModel): BrandModel {
        return brandModelRepository.save(brandModel)
    }

    override fun findByBrandAndModel(brand: String, model: String): BrandModel? {
        return brandModelRepository.findByBrandAndModel(Brand(brand), model)
    }

    override fun getModelsByBrand(brand: String): List<String> {
        return brandModelRepository.findByBrand(Brand(brand)).map { it.model }
    }

    override fun findOrCreateBrandModel(brand: String, model: String): BrandModel {

        var brand = brandService.findOrCreateBrand(brand)

        // Check if the brandModel (brand + model) exists
        var brandModel = findByBrandAndModel(brand.name, model)
        if (brandModel == null) {
            // If brandModel does not exist, create and save a new one
            brandModel = addBrandModel(BrandModel(brand = brand, model = model))
        }
        return brandModel
    }

}