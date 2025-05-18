package g11.reservationservice.services

import g11.reservationservice.entities.Segment

interface SegmentService {
    fun segmentExists(segmentName: String): Boolean
    fun getAllSegments(): List<String>
    fun addSegment(segmentName: String): Segment
    fun findSegmentByName(segmentName: String): Segment?
    fun findOrCreateSegment(segmentName: String): Segment
}