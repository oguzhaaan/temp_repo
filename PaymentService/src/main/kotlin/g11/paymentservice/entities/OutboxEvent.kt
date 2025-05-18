package g11.paymentservice.entities

import jakarta.persistence.*

import java.time.LocalDateTime

@Entity
@Table(name = "paypal_outbox_events")
class OutboxEvent(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "aggregate_id")
    val aggregateId: String,

    @Column(name = "aggregate_type")
    val aggregateType: String,

    @Column(name = "event_type")
    val eventType: String,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
)



