package g11.reservationservice.services

import g11.reservationservice.dtos.PaymentInitiationRequest
import g11.reservationservice.dtos.PaymentInitiationResponse

interface PaymentServiceClient {
    fun initiatePayment(request: PaymentInitiationRequest): PaymentInitiationResponse
}