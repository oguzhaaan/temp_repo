package g11.reservationservice.repositories

import g11.reservationservice.entities.Brand
import org.springframework.data.jpa.repository.JpaRepository

interface BrandRepository: JpaRepository<Brand, String> {
    fun findByName(brandName: String): Brand?
}