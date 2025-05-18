package g11.paymentservice.dtos

data class PaymentInitiationResponse(
    val paymentId: Long,
    val approvalUrl: String
)
