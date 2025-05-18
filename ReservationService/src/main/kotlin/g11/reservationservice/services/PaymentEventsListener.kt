package g11.reservationservice.services

interface PaymentEventsListener {
    fun handlePaymentCompletedEvent(message: String)
}