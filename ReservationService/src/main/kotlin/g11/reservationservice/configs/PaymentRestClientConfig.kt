package g11.reservationservice.configs

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
@ConfigurationProperties(prefix = "payment-service")
class PaymentRestClientConfig {
    @Value("\${payment.service.base-url:http://localhost:8085}")
    lateinit var baseUrl: String

    @Bean
    @Qualifier("paymentRestClient")
    fun paymentRestClient(builder: RestClient.Builder): RestClient {
        return builder.baseUrl(baseUrl).build()
    }
}