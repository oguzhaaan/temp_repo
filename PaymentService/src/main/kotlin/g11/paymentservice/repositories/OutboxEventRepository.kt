package g11.paymentservice.repositories

import g11.paymentservice.entities.OutboxEvent
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface OutboxEventRepository : JpaRepository<OutboxEvent, UUID>
