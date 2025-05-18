package g11.reservationservice.services.impl

import g11.reservationservice.entities.Segment
import g11.reservationservice.repositories.SegmentRepository
import g11.reservationservice.services.SegmentService
import org.springframework.stereotype.Service

@Service
class SegmentServiceImpl(private val segmentRepository: SegmentRepository): SegmentService {
    override fun segmentExists(segmentName: String): Boolean {
        return segmentRepository.existsById(segmentName)
    }

    override fun getAllSegments(): List<String> {
        return segmentRepository.findAll().map { it.name }
    }

    override fun addSegment(segmentName: String): Segment {
        // Check if the segment already exists
        if(segmentRepository.existsById(segmentName)) {
            throw IllegalArgumentException("Segment already exists")
        }
        val newSegment = Segment(segmentName)
        return segmentRepository.save(newSegment)
    }

    override fun findSegmentByName(segmentName: String): Segment? {
        return segmentRepository.findByName(segmentName)
    }

    override fun findOrCreateSegment(segmentName: String): Segment {
        return findSegmentByName(segmentName) ?: addSegment(segmentName)
    }
}