package g11.reservationservice.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import g11.reservationservice.dtos.*
import g11.reservationservice.entities.enumerations.ReservationStatus
import g11.reservationservice.entities.enumerations.VehicleStatus
import g11.reservationservice.exceptions.ReservationNotFoundException
import g11.reservationservice.services.ReservationService
import g11.reservationservice.unit.controllers.ReservationControllerTestConfig
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate
import java.time.LocalDateTime

@WebMvcTest(ReservationController::class)
@Import(ReservationControllerTestConfig::class)
class ReservationControllerIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var reservationService: ReservationService

    @Test
    fun `createReservation should return 201 with Location header`() {
        val userId = 1L
        val carModelId = 2L
        val startDate = LocalDate.of(2025, 9, 10)
        val endDate = LocalDate.of(2025, 9, 15)

        val request = ReservationTimeIntervalDTO(
            startDate = startDate,
            endDate = endDate
        )

        val carModelResponseDTO = CarModelResponseDTO(
            id = carModelId,
            brand = "Toyota",
            model = "Corolla",
            modelYear = 2023,
            segment = "Compact",
            numberOfDoors = 4,
            seatingCapacity = 5,
            luggageCapacity = 3,
            category = "Economy",
            engineType = "Petrol",
            transmissionType = "Automatic",
            drivetrain = "FWD",
            motorDisplacement = 1.8,
            features = mutableSetOf("Bluetooth", "Air Conditioning"),
            rentalPricePerDay = 50.0f
        )

        val vehicleResponseDTO = VehicleResponseDTO(
            id = 10L,
            carModel = carModelResponseDTO,
            licensePlate = "XYZ-123",
            vin = "1HGCM82633A004352",
            kilometersTravelled = 12000,
            vehicleStatus = VehicleStatus.AVAILABLE
        )

        val response = ReservationResponseDTO(
            reservationId = 100L,
            userId = userId,
            vehicle = vehicleResponseDTO,
            startDate = startDate,
            endDate = endDate,
            reservationStatus = ReservationStatus.CONFIRMED,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            totalPrice = 250.0f,
            cancellationDate = null,
            pickup = null,
            dropoff = null
        )

        whenever(reservationService.createReservation(eq(userId), eq(carModelId), any()))
            .thenReturn(response)

//        /users/{customerId}/cars/{carModelId}
        val url = "/api/v1/reservations/users/$userId/cars/$carModelId"

        mockMvc.post(url) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect { status { isCreated() } }
            .andExpect { header { exists("Location") } }
            .andExpect { jsonPath("$.reservationId").value(100) }
    }


    @Test
    fun `getReservationById should return 200 and reservation details`() {
        val response = buildResponse()

        whenever(reservationService.getReservationById(100L)).thenReturn(response)

        mockMvc.get("/api/v1/reservations/100") {
            accept = MediaType.APPLICATION_JSON
        }
            .andExpect { status { isOk() } }
            .andExpect { jsonPath("$.reservationId").value(100) }
            .andExpect { jsonPath("$.userId").value(1) }
            .andExpect { jsonPath("$.vehicle.id").value(10) }
            .andExpect { jsonPath("$.vehicle.carModel.brand").value("Toyota") }
            .andExpect { jsonPath("$.startDate").value("2025-05-10") }
            .andExpect { jsonPath("$.endDate").value("2025-05-15") }
            .andExpect { jsonPath("$.reservationStatus").value("CONFIRMED") }
    }

    @Test
    fun `updateReservationStatus should return 200 and updated reservation`() {
        val updated = buildResponse(
            reservationStatus = ReservationStatus.CANCELLED,
            cancellationDate = LocalDateTime.of(2025, 5, 1, 12, 25),
            updatedAt = LocalDateTime.of(2025, 5, 1, 12, 30)
        )

        whenever(reservationService.updateReservationStatus(100L, ReservationStatus.CANCELLED))
            .thenReturn(updated)

        mockMvc.put("/api/v1/reservations/100/status/CANCELLED") {
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect { status { isOk() } }
            .andExpect { jsonPath("$.reservationId").value(100) }
            .andExpect { jsonPath("$.reservationStatus").value("CANCELLED") }
            .andExpect { jsonPath("$.cancellationDate").exists() }
    }

    @Test
    fun `updateReservationStatus should return 404 when reservation not found`() {
        val reservationId = 999L
        whenever(reservationService.updateReservationStatus(reservationId, ReservationStatus.CANCELLED))
            .thenThrow(ReservationNotFoundException(reservationId))

        mockMvc.put("/api/v1/reservations/$reservationId/status/CANCELLED") {
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect { status { isNotFound() } }
            .andExpect {
                jsonPath("$.message").value("Reservation with ID $reservationId not found")
            }
    }

    private fun buildResponse(
        reservationId: Long = 100L,
        userId: Long = 1L,
        carModelId: Long = 2L,
        reservationStatus: ReservationStatus = ReservationStatus.CONFIRMED,
        cancellationDate: LocalDateTime? = null,
        updatedAt: LocalDateTime? = null
    ): ReservationResponseDTO {
        val carModel = CarModelResponseDTO(
            id = carModelId,
            brand = "Toyota",
            model = "Corolla",
            modelYear = 2023,
            segment = "Compact",
            numberOfDoors = 4,
            seatingCapacity = 5,
            luggageCapacity = 3,
            category = "Economy",
            engineType = "Petrol",
            transmissionType = "Automatic",
            drivetrain = "FWD",
            motorDisplacement = 1.8,
            features = mutableSetOf("Bluetooth", "Air Conditioning"),
            rentalPricePerDay = 50.0f
        )
        val vehicle = VehicleResponseDTO(
            id = 10L,
            carModel = carModel,
            licensePlate = "XYZ-123",
            vin = "1HGCM82633A004352",
            kilometersTravelled = 12000,
            vehicleStatus = VehicleStatus.AVAILABLE
        )
        return ReservationResponseDTO(
            reservationId = reservationId,
            userId = userId,
            vehicle = vehicle,
            startDate = LocalDate.of(2025, 5, 10),
            endDate = LocalDate.of(2025, 5, 15),
            reservationStatus = reservationStatus,
            createdAt = LocalDateTime.of(2025, 5, 1, 12, 0),
            updatedAt = updatedAt,
            totalPrice = 250.0f,
            cancellationDate = cancellationDate,
            pickup = null,
            dropoff = null
        )
    }

    @Test
    fun `deleteReservation should return 204 when reservation exists`() {
        val reservationId = 42L

        // Mock the service method (void return)
        doNothing().whenever(reservationService).deleteReservation(reservationId)

        val url = "/api/v1/reservations/$reservationId"

        mockMvc.delete(url)
            .andExpect {
                status { isNoContent() }
            }
    }

    @Test
    fun `deleteReservation should return 404 when reservation does not exist`() {
        val reservationId = 999L

        whenever(reservationService.deleteReservation(reservationId))
            .thenThrow(ReservationNotFoundException(reservationId))

        val url = "/api/v1/reservations/$reservationId"

        mockMvc.delete(url)
            .andExpect {
                status { isNotFound() }
            }
    }





}
