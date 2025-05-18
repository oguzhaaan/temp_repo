package g11.reservationservice.services.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import g11.reservationservice.entities.enumerations.ReservationStatus
import g11.reservationservice.repositories.ReservationRepository
import g11.reservationservice.services.PaymentEventsListener
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class PaymentEventsListenerImpl(
    private val objectMapper: ObjectMapper,
    private val reservationRepository: ReservationRepository
): PaymentEventsListener {
    // log
    private val logger = org.slf4j.LoggerFactory.getLogger(PaymentEventsListenerImpl::class.java)

    @KafkaListener(topics = ["payment-events"], groupId = "reservation-service-group")
    override fun handlePaymentCompletedEvent(message: String) {
        val payload: Map<String, Any> = objectMapper.readValue(message, object : TypeReference<Map<String, Any>>() {})
        val reservationId = (payload["reservationId"] as Number).toLong()

        val reservation = reservationRepository.findById(reservationId).orElse(null)
        if (reservation != null) {
            reservation.reservationStatus = ReservationStatus.CONFIRMED
            reservationRepository.save(reservation)
        }
        logger.info("Payment completed for reservationId=$reservationId")
    }
}
