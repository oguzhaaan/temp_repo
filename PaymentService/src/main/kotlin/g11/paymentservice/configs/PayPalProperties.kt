package g11.paymentservice.configs

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "paypal")
class PayPalProperties {
    lateinit var clientId: String
    lateinit var clientSecret: String
    lateinit var environment: String
    lateinit var returnUrl: String
    lateinit var cancelUrl: String
}
