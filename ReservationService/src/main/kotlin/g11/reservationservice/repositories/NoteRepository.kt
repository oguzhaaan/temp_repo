package g11.reservationservice.repositories

import g11.reservationservice.entities.Note
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface NoteRepository: JpaRepository<Note, Long>, JpaSpecificationExecutor<Note>