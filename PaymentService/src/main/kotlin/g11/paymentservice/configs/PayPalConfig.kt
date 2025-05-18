package g11.paymentservice.configs

import com.paypal.sdk.Environment
import com.paypal.sdk.PaypalServerSdkClient
import com.paypal.sdk.authentication.ClientCredentialsAuthModel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.slf4j.event.Level

@Configuration
class PayPalConfig(private val payPalProperties: PayPalProperties) {
    @Bean
    fun payPalClient(): PaypalServerSdkClient {

        val environment = when (payPalProperties.environment) {
            "sandbox" -> Environment.SANDBOX
            "production" -> Environment.PRODUCTION
            else -> throw IllegalArgumentException("Invalid PayPal environment: ${payPalProperties.environment}")
        }

        return PaypalServerSdkClient.Builder()
            .loggingConfig { builder ->
                builder.level(Level.DEBUG)
                    .requestConfig { logConfigBuilder ->
                        logConfigBuilder.body(true)
                    }
                    .responseConfig { logConfigBuilder -> logConfigBuilder.headers(true) }
            }
            .httpClientConfig { configBuilder ->
                configBuilder.timeout(5000) // 5 seconds timeout
            }
            .clientCredentialsAuth(
                ClientCredentialsAuthModel.Builder(payPalProperties.clientId, payPalProperties.clientSecret).build()
            )
            .environment(environment)
            .build()
    }
}