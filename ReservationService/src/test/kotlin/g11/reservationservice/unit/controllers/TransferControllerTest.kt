package g11.reservationservice.unit.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import g11.reservationservice.controllers.TransferController
import g11.reservationservice.dtos.TransferDTO
import g11.reservationservice.dtos.TransferResponseDTO
import g11.reservationservice.entities.enumerations.TransferType
import g11.reservationservice.exceptions.ReservationNotFoundException
import g11.reservationservice.exceptions.TransferDateMismatchException
import g11.reservationservice.exceptions.TransferNotFoundException
import g11.reservationservice.services.TransferService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.mockito.Mockito.verify
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

@WebMvcTest(TransferController::class)
class TransferControllerIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockitoBean
    lateinit var transferService: TransferService

    @Test
    fun `createTransfer should return 201 with full TransferResponseDTO for DROPOFF`() {
        // Arrange
        val reservationId = 123L
        val vehicleId = 456L
        val transferTime = LocalDateTime.now().plusDays(2)
        val location = "Hotel Main Gate"
        val staffId = 42L

        val requestDTO = TransferDTO(
            transferTime = transferTime,
            location = location,
            handledByStaffId = staffId
        )

        val responseDTO = TransferResponseDTO(
            reservationId = reservationId,
            vehicleId = vehicleId,
            location = location,
            transferTime = transferTime,
            handledByStaffId = staffId
        )

        `when`(
            transferService.createTransfer(
                TransferType.DROPOFF,
                reservationId,
                requestDTO
            )
        ).thenReturn(responseDTO)

        // Act & Assert
        mockMvc.perform(
            post("/api/v1/transfers/DROPOFF/$reservationId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.reservationId").value(reservationId))
            .andExpect(jsonPath("$.vehicleId").value(vehicleId))
            .andExpect(jsonPath("$.location").value(location))
            .andExpect(jsonPath("$.transferTime").value(org.hamcrest.Matchers.startsWith(transferTime.toString().substring(0, 19))))
            .andExpect(jsonPath("$.handledByStaffId").value(staffId))
    }

    @Test
    fun `updateTransfer should return 200 with updated TransferResponseDTO`() {
        // Arrange
        val reservationId = 123L
        val vehicleId = 456L
        val type = TransferType.DROPOFF
        val requestDTO = TransferDTO(
            transferTime = LocalDateTime.now().plusDays(2),
            location = "Updated Location",
            handledByStaffId = 99L
        )

        val responseDTO = TransferResponseDTO(
            reservationId = reservationId,
            vehicleId = vehicleId,
            location = requestDTO.location,
            transferTime = requestDTO.transferTime,
            handledByStaffId = requestDTO.handledByStaffId
        )

        `when`(transferService.updateTransfer(type, reservationId, requestDTO)).thenReturn(responseDTO)

        // Act & Assert
        mockMvc.perform(
            put("/api/v1/transfers/${type.name}/$reservationId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.reservationId").value(reservationId))
            .andExpect(jsonPath("$.vehicleId").value(vehicleId))
            .andExpect(jsonPath("$.location").value(requestDTO.location))
    }

    @Test
    fun `deleteTransfer should return 204 when successfully deleted`() {
        val transferId = 999L
        val type = TransferType.PICKUP

        mockMvc.perform(
            delete("/api/v1/transfers/${type.name}/$transferId")
        )
            .andExpect(status().isNoContent)

        verify(transferService).deleteTransfer(type, transferId)
    }

    @Test
    fun `getTransferByReservationId should return 200 with TransferResponseDTO`() {
        val reservationId = 321L
        val vehicleId = 654L
        val type = TransferType.PICKUP

        val responseDTO = TransferResponseDTO(
            reservationId = reservationId,
            vehicleId = vehicleId,
            location = "Pickup Location",
            transferTime = LocalDateTime.now().plusDays(1),
            handledByStaffId = 101L
        )

        `when`(transferService.getTransferById(type, reservationId)).thenReturn(responseDTO)

        mockMvc.perform(get("/api/v1/transfers/${type.name}/$reservationId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.reservationId").value(reservationId))
            .andExpect(jsonPath("$.vehicleId").value(vehicleId))
            .andExpect(jsonPath("$.location").value("Pickup Location"))
    }

    @Test
    fun `createTransfer should return 404 if reservation not found`() {
        val reservationId = 999L
        val type = TransferType.PICKUP
        val requestDTO = TransferDTO(
            transferTime = LocalDateTime.now().plusDays(1),
            location = "Terminal 1",
            handledByStaffId = 42L
        )

        `when`(transferService.createTransfer(type, reservationId, requestDTO))
            .thenThrow(ReservationNotFoundException(reservationId))

        mockMvc.perform(
            post("/api/v1/transfers/${type.name}/$reservationId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO))
        )
            .andExpect(status().isNotFound)
    }
    @Test
    fun `updateTransfer should return 400 for invalid transfer time`() {
        val reservationId = 123L
        val type = TransferType.DROPOFF
        val requestDTO = TransferDTO(
            transferTime = LocalDateTime.now().plusDays(5), // not matching end date
            location = "Too Late",
            handledByStaffId = 42L
        )

        `when`(transferService.updateTransfer(type, reservationId, requestDTO))
            .thenThrow(TransferDateMismatchException("Dropoff date must match reservation end date"))

        mockMvc.perform(
            put("/api/v1/transfers/${type.name}/$reservationId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `deleteTransfer should return 404 if transfer not found`() {
        val transferId = 404L
        val type = TransferType.DROPOFF

        `when`(transferService.deleteTransfer(type, transferId))
            .thenThrow(TransferNotFoundException("Not Found"))

        mockMvc.perform(delete("/api/v1/transfers/${type.name}/$transferId"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `getTransferByReservationId should return 404 if not found`() {
        val reservationId = 999L
        val type = TransferType.DROPOFF

        `when`(transferService.getTransferById(type, reservationId))
            .thenThrow(TransferNotFoundException("Not Found"))

        mockMvc.perform(get("/api/v1/transfers/${type.name}/$reservationId"))
            .andExpect(status().isNotFound)
    }

}

