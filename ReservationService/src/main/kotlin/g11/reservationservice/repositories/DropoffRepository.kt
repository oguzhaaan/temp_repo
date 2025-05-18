package g11.reservationservice.repositories

import g11.reservationservice.entities.Dropoff
import org.springframework.data.jpa.repository.JpaRepository

interface DropoffRepository: JpaRepository<Dropoff, Long> {
}