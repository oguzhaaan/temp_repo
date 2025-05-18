package g11.reservationservice.services

import g11.reservationservice.entities.Brand

interface BrandService {
    fun getAllBrands(): List<String>
    fun findBrandByName(brandName: String): Brand?
    fun brandExists(brandName: String): Boolean
    fun addBrand(brandName: String): Brand
    fun findOrCreateBrand(brandName :String): Brand
    fun deleteAllBrands()
}