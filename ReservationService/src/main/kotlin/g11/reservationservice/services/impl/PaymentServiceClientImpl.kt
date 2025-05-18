package g11.reservationservice.services.impl

import g11.reservationservice.dtos.PaymentInitiationRequest
import g11.reservationservice.dtos.PaymentInitiationResponse
import g11.reservationservice.services.PaymentServiceClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class PaymentServiceClientImpl(
    @Qualifier("paymentRestClient") private val restClient: RestClient
): PaymentServiceClient {
    override fun initiatePayment(request: PaymentInitiationRequest): PaymentInitiationResponse {
        return restClient.post()
            .uri("/payments/initiate")
            .body(request)
            .retrieve()
            .body(PaymentInitiationResponse::class.java)!!
    }
}
