package g11.paymentservice.dtos

import com.paypal.sdk.models.Order

data class PaypalPaymentResult(
    val success: Boolean,
    val transactionId: String?,
    val rawResponse: Order?,
    val errorMessage: String?
)


