package g11.paymentservice.dtos

data class PaymentConfirmationResponse(
    val success: Boolean,
    val message: String,
    val reservationId: Long? = null
)
