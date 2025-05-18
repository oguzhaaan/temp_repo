package g11.paymentservice.dtos

data class PaymentConfirmationRequest(
    val orderId: String // This is PayPal's transaction ID
)

