package g11.reservationservice.entities

import g11.reservationservice.entities.enumerations.NoteType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "note")
class Note(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    var vehicle: Vehicle,

    @Column(name = "note", nullable = false)
    var note: String,

    @Column(name = "date", nullable = false)
    var date: LocalDateTime,

    @Column(name = "author", nullable = false)
    var author: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: NoteType

) : BaseEntity<Long>()