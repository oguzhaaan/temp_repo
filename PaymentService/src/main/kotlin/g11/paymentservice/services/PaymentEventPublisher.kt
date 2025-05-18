package g11.paymentservice.services

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

import com.fasterxml.jackson.databind.ObjectMapper
import g11.paymentservice.dtos.PaymentEvent


@Service
class PaymentEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {
    fun publishPaymentCompletedEvent(payload: PaymentEvent, aggregateId: String) {
        val topic = "payment-events"
        val payloadString = objectMapper.writeValueAsString(payload)
        kafkaTemplate.send(topic, aggregateId, payloadString)
    }
}


