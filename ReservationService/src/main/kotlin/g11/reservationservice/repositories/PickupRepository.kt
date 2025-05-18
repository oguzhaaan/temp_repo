package g11.reservationservice.repositories

import g11.reservationservice.entities.Pickup
import org.springframework.data.jpa.repository.JpaRepository

interface PickupRepository: JpaRepository<Pickup, Long>
