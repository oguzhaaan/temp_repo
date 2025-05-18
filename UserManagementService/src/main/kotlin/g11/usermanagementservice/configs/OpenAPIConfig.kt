package g11.usermanagementservice.configs

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenAPIConfig {
    @Bean
    fun getOpenApiDocumentation(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("User Management Service API")
                .description("API documentation for the User Management Service")
                .version("v1.0")
        )

}