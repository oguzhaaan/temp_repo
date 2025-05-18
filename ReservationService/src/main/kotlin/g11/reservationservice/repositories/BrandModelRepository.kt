package g11.reservationservice.repositories

import g11.reservationservice.entities.Brand
import g11.reservationservice.entities.BrandModel
import org.springframework.data.jpa.repository.JpaRepository

interface BrandModelRepository: JpaRepository<BrandModel, Long> {
    fun findByBrandAndModel(brand: Brand, model: String): BrandModel?
    fun findByBrand(brand: Brand): List<BrandModel>
}