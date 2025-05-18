package g11.paymentservice.entities

import g11.paymentservice.entities.enumerations.PaymentStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "payment")
class Payment(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long,

    @Column(nullable = false)
    val reservationId: Long,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val amount: Float,

    @Column(nullable = false)
    val currency: String = "EUR",

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: PaymentStatus,

    val method: String = "PAYPAL",

    val paypalTransactionId: String? = null,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    var updatedAt: LocalDateTime?
)


