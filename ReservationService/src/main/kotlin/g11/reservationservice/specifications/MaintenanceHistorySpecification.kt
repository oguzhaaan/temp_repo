package g11.reservationservice.specifications

import g11.reservationservice.dtos.MaintenanceHistoryFilterDTO
import g11.reservationservice.entities.MaintenanceHistory
import g11.reservationservice.entities.enumerations.MaintenanceStatus
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate
import jakarta.persistence.criteria.Predicate

object MaintenanceHistorySpecification {

    fun fromFilter(filter: MaintenanceHistoryFilterDTO): Specification<MaintenanceHistory> {
        return Specification.where<MaintenanceHistory>(null)
            .and(statusIn(filter.status))
            .and(dateBetween(filter.startDate, filter.endDate))
    }

    private fun statusIn(statuses: List<MaintenanceStatus>?): Specification<MaintenanceHistory>? {
        if (statuses.isNullOrEmpty()) return null
        return Specification { root, _, cb ->
            root.get<MaintenanceStatus>("maintenanceStatus").`in`(statuses)
        }
    }

    private fun dateBetween(start: LocalDate?, end: LocalDate?): Specification<MaintenanceHistory>? {
        return Specification { root, _, cb ->
            val predicates = mutableListOf<Predicate>()
            start?.let { predicates.add(cb.greaterThanOrEqualTo(root.get("maintenanceDate"), java.sql.Date.valueOf(it))) }
            end?.let { predicates.add(cb.lessThanOrEqualTo(root.get("maintenanceDate"), java.sql.Date.valueOf(it))) }
            cb.and(*predicates.toTypedArray())
        }
    }
}
