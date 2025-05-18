package g11.reservationservice.unit.controllers

import g11.reservationservice.services.ReservationService
import org.mockito.kotlin.mock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class ReservationControllerTestConfig {
    @Bean
    fun reservationService(): ReservationService = mock()
}
