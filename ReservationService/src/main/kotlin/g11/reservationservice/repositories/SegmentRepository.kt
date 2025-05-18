package g11.reservationservice.repositories

import g11.reservationservice.entities.Feature
import g11.reservationservice.entities.Segment
import org.springframework.data.jpa.repository.JpaRepository

interface SegmentRepository: JpaRepository<Segment, String> {
    fun findByName(segmentName: String): Segment?
}