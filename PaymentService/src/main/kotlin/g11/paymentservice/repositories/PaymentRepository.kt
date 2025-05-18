package g11.paymentservice.repositories

import g11.paymentservice.entities.Payment
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentRepository : JpaRepository<Payment, Long> {
    fun findByPaypalTransactionId(paypalTransactionId: String): Payment?
}
