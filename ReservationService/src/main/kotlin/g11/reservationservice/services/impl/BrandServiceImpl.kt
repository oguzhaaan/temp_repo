package g11.reservationservice.services.impl

import g11.reservationservice.entities.Brand
import g11.reservationservice.repositories.BrandRepository
import g11.reservationservice.services.BrandService
import org.springframework.stereotype.Service

@Service
class BrandServiceImpl(
    private val brandRepository: BrandRepository
) : BrandService {

    override fun getAllBrands(): List<String> {
        return brandRepository.findAll().map { it.name }
    }

    override fun findBrandByName(brandName: String): Brand? {
        return brandRepository.findByName(brandName)
    }

    override fun brandExists(brandName: String): Boolean {
        return brandRepository.existsById(brandName)
    }

    override fun addBrand(brandName: String): Brand {
        // Check if the brand already exists
        if (brandExists(brandName)) {
            throw IllegalArgumentException("Brand already exists")
        }

        val newBrand = Brand(brandName)
        return brandRepository.save(newBrand)
    }

    override fun findOrCreateBrand(brandName :String): Brand {
        return findBrandByName(brandName) ?: addBrand(brandName)
    }

    override fun deleteAllBrands(){
        return brandRepository.deleteAll()
    }
}