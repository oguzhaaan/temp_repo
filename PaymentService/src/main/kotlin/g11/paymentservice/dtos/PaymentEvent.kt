package g11.paymentservice.dtos

import g11.paymentservice.entities.enumerations.PaymentStatus

data class PaymentEvent(
    val reservationId: Long,
    val userId: Long,
    val amount: Float,
    val status: PaymentStatus
)


