package g11.reservationservice.dtos

data class PaymentInitiationRequest(
    val reservationId: Long,
    val userId: Long,
    val amount: Float,
    val currency: String = "EUR"
)

