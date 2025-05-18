package g11.reservationservice.specifications

import g11.reservationservice.dtos.NoteFilterDTO
import g11.reservationservice.entities.Note
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate

object NoteSpecification {

    fun fromFilter(filter: NoteFilterDTO): Specification<Note> {
        return Specification.where(authorEquals(filter.author))
            .and(dateBetween(filter.startDate, filter.endDate))
    }

    private fun authorEquals(author: String?): Specification<Note>? {
        if (author.isNullOrBlank()) return null
        return Specification { root, _, cb ->
            cb.equal(cb.lower(root.get("author")), author.lowercase())
        }
    }

    private fun dateBetween(start: LocalDate?, end: LocalDate?): Specification<Note>? {
        return Specification { root, _, cb ->
            val predicates = mutableListOf<Predicate>()
            start?.let {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), java.sql.Date.valueOf(it)))
            }
            end?.let {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), java.sql.Date.valueOf(it)))
            }
            cb.and(*predicates.toTypedArray())
        }
    }
}
