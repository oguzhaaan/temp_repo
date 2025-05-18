package g11.reservationservice.configs

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
@ConfigurationProperties(prefix = "user-service")
class UserRestClientConfig {
    @Value("\${user-service.base-url:http://localhost:8082/api/users}")
    lateinit var baseUrl: String

    @Bean
    @Qualifier("userRestClient")
    fun userRestClient(builder: RestClient.Builder): RestClient {
        return builder.baseUrl(baseUrl).build()
    }
}

