package g11.reservationservice.dtos

data class PaymentInitiationResponse(
    val paymentId: Long,
    val approvalUrl: String
)
