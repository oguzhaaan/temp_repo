package g11.reservationservice

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import g11.reservationservice.dtos.VehicleRequestDTO
import g11.reservationservice.dtos.VehicleResponseDTO
import g11.reservationservice.entities.Vehicle
import g11.reservationservice.entities.enumerations.VehicleStatus
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import java.awt.PageAttributes

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReservationFullNetworkTests: IntegrationTest() {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    fun `getting all Brands`(){
        val response: ResponseEntity<String> = restTemplate.
        getForEntity("/api/v1/brands", String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())

        val mapper = jacksonObjectMapper()
        val brands: List<String> = mapper.readValue(response.body!!)

        assert(brands.contains("Fiat"))
        assert(brands.size == 13)
    }

    @Test
    fun `getting all Models`(){
        val response: ResponseEntity<String> = restTemplate.
        getForEntity("/api/v1/brandModels", String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())

        val mapper = jacksonObjectMapper()
        val brandsModels: Map<String, List<String>> = mapper.readValue(response.body!!)

        assert(brandsModels.containsKey("Fiat"))
        assert(brandsModels["Fiat"]?.contains("500") == true)
        assert(brandsModels["Fiat"]?.contains("Punto") == true)
        assert(brandsModels.size == 13)
    }

}