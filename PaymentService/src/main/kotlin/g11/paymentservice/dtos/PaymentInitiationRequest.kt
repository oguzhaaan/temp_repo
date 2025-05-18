package g11.paymentservice.dtos

data class PaymentInitiationRequest(
    val reservationId: Long,
    val userId: Long,
    val amount: Float,
    val currency: String = "EUR"
)

