/*package g11.usermanagementservice.controller

import g11.usermanagementservice.dtos.UserRequestDTO
import g11.usermanagementservice.entities.enumerations.Role
import g11.usermanagementservice.entities.User
import g11.usermanagementservice.repositories.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.*
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class UserControllerIntegrationTest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var userRepository: UserRepository

    companion object {
        @Container
        val postgres = PostgreSQLContainer<Nothing>("postgres:15").apply {
            withDatabaseName("testdb")
            withUsername("testuser")
            withPassword("testpass")
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }

    @Test
    @Order(1)
    fun `should create a new user and retrieve it`() {
        // Arrange
        val userRequest = UserRequestDTO(
            username = "testuser",
            firstName = "Test",
            lastName = "User",
            email = "testuser@example.com",
            phoneNumber = "1234567890",
            birthDate = LocalDate.of(1995, 5, 15),
            customerProfile = null
        )

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val requestEntity = HttpEntity(userRequest, headers)

        // Act 1: Create user
        val createResponse = restTemplate.postForEntity(
            "http://localhost:$port/api/v1/users?role=CUSTOMER",
            requestEntity,
            Map::class.java
        )

        // Assert create
        assertEquals(HttpStatus.CREATED, createResponse.statusCode)

        // Extract Location header
        val location = createResponse.headers.location
        requireNotNull(location) { "Location header should not be null" }

        // Act 2: Retrieve user
        val getResponse = restTemplate.getForEntity(
            "http://localhost:$port${location.path}",
            Map::class.java
        )

        // Assert retrieve
        assertEquals(HttpStatus.OK, getResponse.statusCode)
        assertEquals("testuser", (getResponse.body?.get("username") as String))
        assertEquals("testuser@example.com", (getResponse.body?.get("email") as String))
    }
}
*/