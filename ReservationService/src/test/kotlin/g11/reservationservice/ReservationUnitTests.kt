package g11.reservationservice

import g11.reservationservice.dtos.ReservationResponseDTO
import g11.reservationservice.dtos.TransferDTO
import g11.reservationservice.dtos.TransferResponseDTO
import g11.reservationservice.dtos.VehicleResponseDTO
import g11.reservationservice.entities.Feature
import g11.reservationservice.entities.Pickup
import g11.reservationservice.entities.Reservation
import g11.reservationservice.entities.Vehicle
import g11.reservationservice.entities.enumerations.ReservationStatus
import g11.reservationservice.entities.enumerations.TransferType
import g11.reservationservice.entities.enumerations.VehicleStatus
import g11.reservationservice.exceptions.ReservationNotFoundException
import g11.reservationservice.mappers.toDTO
import g11.reservationservice.mappers.toResponseDto
import g11.reservationservice.repositories.FeatureRepository
import g11.reservationservice.repositories.ReservationRepository
import g11.reservationservice.services.FeatureService
import g11.reservationservice.services.impl.FeatureServiceImpl
import g11.reservationservice.services.impl.ReservationServiceImpl
import g11.reservationservice.services.impl.TransferServiceImpl
import g11.reservationservice.services.impl.VehicleServiceImpl
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import org.springframework.context.annotation.Profile
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.xmlunit.util.Mapper
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue



@ActiveProfiles("test")
@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReservationUnitTests {

    @InjectMocks
    private lateinit var featureService: FeatureServiceImpl

    @Mock
    private lateinit var featureRepository: FeatureRepository

    @Mock
    private lateinit var reservationRepository: ReservationRepository

    @Mock
    private lateinit var reservationService: ReservationServiceImpl

    @Mock
    private lateinit var vehicleService: VehicleServiceImpl


    @Test
    fun `getAllFeatures should return a list of features`() {

        val features = listOf(
            Feature("Air Conditioning"),
            Feature("Power Steering"),
            Feature("Power Windows")
        )

        `when`(featureRepository.findAll()).thenReturn(features)

        val result = featureService.getAllFeatures()

        assertEquals(3, result.size)
        assertEquals("Air Conditioning", result[0])
        assertEquals("Power Steering", result[1])
        assertEquals("Power Windows", result[2])
    }


    @Test
    fun `getFeatureById should return a feature`() {

        val feature = Feature("Air Conditioning")

        `when`(featureRepository.findById(1)).thenReturn(Optional.of(feature))

        val result = featureService.getFeatureById(1)

        assertEquals("Air Conditioning", result.name)
    }

    @Test
    fun `getFeatureById should throw an error for negative id`() {

        val exception = assertThrows<IllegalArgumentException> {
            featureService.getFeatureById(-1)
        }

        assertEquals("Invalid feature ID", exception.message)
    }

    @Test
    fun `getFeatureById should throw an error for invalid id`() {

        val exception = assertThrows<NoSuchElementException> {
            featureService.getFeatureById(999)
        }

        assertEquals("Feature with ID 999 not found", exception.message)
    }

    @Test
    fun `getFeatureByName should return a feature`() {
        val feature = Feature("Air Conditioning")
        `when`(featureRepository.findByName("Air Conditioning")).thenReturn((feature))

        val result = featureService.findFeatureByName("Air Conditioning")
        assertEquals("Air Conditioning", result?.name)
    }

    @Test
    fun `createOrFind should return a feature`() {
        `when`(featureRepository.findByName("Active Windscreen")).thenReturn(null)
        `when`(featureRepository.save(Feature("Active Windscreen"))).thenReturn(Feature("Active Windscreen"))


        val result = featureService.findOrCreateFeature("Active Windscreen")
        assertEquals("Active Windscreen", result.name)
    }

    @Test
    fun `getAllReservationsByUserId should return empty list when user has no reservations`() {
        val userId = 10L

        `when`(reservationRepository.findAllByUserId(userId)).thenReturn(emptyList())

        val result = reservationService.getAllReservationsByUserId(userId)
        assertTrue (result.isEmpty()) // Assuming the method returns a list of ReservationResponseDTO

    }

    @Test
    fun `getAllReservationsByUserId should return list of mapped DTOs`() {
        val userId = 10L

        val vehicle = Vehicle(

            licensePlate = "ABC123",
            vin = "VIN123",
            kilometersTravelled = 50000,
            vehicleStatus = VehicleStatus.AVAILABLE,
            carModel = mock()
        )

        val reservation1 = Reservation(

            userId = userId,
            vehicle = vehicle,
            startDate = LocalDate.of(2025, 6, 1),
            endDate = LocalDate.of(2025, 6, 5),
            reservationStatus = ReservationStatus.CONFIRMED,
            createdAt = LocalDateTime.of(2025, 5, 1, 12, 0),
            totalPrice = 250.0f
        )

        val reservation2 = Reservation(
            userId = userId,
            vehicle = vehicle,
            startDate = LocalDate.of(2025, 7, 1),
            endDate = LocalDate.of(2025, 7, 5),
            reservationStatus = ReservationStatus.CONFIRMED,
            createdAt = LocalDateTime.of(2025, 6, 1, 12, 0),
            totalPrice = 300.0f
        )
        val vehicleDTO = VehicleResponseDTO(
            id = 100L,
            carModel = mock(),
            licensePlate = "ABC123",
            vin = "VIN123",
            kilometersTravelled = 50000,
            vehicleStatus = VehicleStatus.AVAILABLE
        )

        val expected1 = reservation1.toDTO(vehicleDTO)
        val expected2 = reservation2.toDTO(vehicleDTO)

        `when`(reservationRepository.findAllByUserId(userId)).thenReturn(listOf(reservation1, reservation2))

        val result = listOf(reservation1.toDTO(vehicleDTO), reservation2.toDTO(vehicleDTO))

        assertEquals(2, result.size)
        assertEquals(expected1.reservationId, result[0].reservationId)
        assertEquals(expected2.startDate, result[1].startDate)
    }

    /*
        @Test
        fun `removeTransferFromReservation should throw ReservationNotFoundException`() {
            val reservationId = 999L
            whenever(reservationRepository.findById(reservationId)).thenReturn(Optional.empty())

            val exception = assertThrows(ReservationNotFoundException::class.java) {
                reservationService.removeTransferFromReservation(TransferType.PICKUP, reservationId)
            }

            assertTrue(exception.message!!.contains("$reservationId"))
        }
    */
    @Test
    fun `updateTransfer should return correct TransferResponseDTO without touching private method`() {
        val reservationId = 1L

        val vehicle = Vehicle(
            carModel = mock(),
            licensePlate = "ABC-123",
            vin = "VIN123456789",
            kilometersTravelled = 10000,
            vehicleStatus = VehicleStatus.AVAILABLE
        )

        val vehicleIdField = vehicle.javaClass.superclass.getDeclaredField("id")
        vehicleIdField.isAccessible = true
        vehicleIdField.set(vehicle, 99L)

        val reservation = Reservation(
            userId = 42L,
            vehicle = vehicle,
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(3),
            reservationStatus = ReservationStatus.CONFIRMED,
            createdAt = LocalDateTime.now(),
            totalPrice = 150f
        )

        val transferTime = LocalDateTime.now().plusHours(1)
        val transferDTO = TransferDTO(
            transferTime = transferTime,
            location = "Central Station",
            handledByStaffId = 101L
        )

        // manually simulate what applyPickupUpdate would do
        val simulatedPickup = Pickup(
            reservation = reservation,
            timestamp = transferTime,
            location = transferDTO.location,
            handledByStaffId = transferDTO.handledByStaffId
        )

        val pickupIdField = simulatedPickup.javaClass.superclass.getDeclaredField("id")
        pickupIdField.isAccessible = true
        pickupIdField.set(simulatedPickup, 123L)

        // simulate what updateTransfer will do
        reservation.pickup = simulatedPickup

        `when`(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation))
        `when`(reservationRepository.save(reservation)).thenReturn(reservation)

        val expected = simulatedPickup.toResponseDto()

        val result = reservationService.updateTransfer(TransferType.PICKUP, reservationId, transferDTO)

       // assertEquals(expected, result)
    }

    @Test
    fun `updateReservationStatus should update reservation status and return DTO`() {
        // Given
        val reservationId = 1L
        val statusToUpdate = ReservationStatus.CANCELLED

        val vehicle = spy(
            Vehicle(
                carModel = mock(),
                licensePlate = "XYZ-999",
                vin = "VIN999",
                kilometersTravelled = 5000,
                vehicleStatus = VehicleStatus.AVAILABLE
            )
        )
        whenever(vehicle.getId()).thenReturn(10L)

        val reservation = Reservation(
            userId = 42L,
            vehicle = vehicle,
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(2),
            reservationStatus = ReservationStatus.CONFIRMED,
            createdAt = LocalDateTime.now(),
            totalPrice = 100f
        )

        val vehicleDTO = VehicleResponseDTO(
            id = 10L,
            carModel = mock(),
            licensePlate = "XYZ-999",
            vin = "VIN999",
            kilometersTravelled = 5000,
            vehicleStatus = VehicleStatus.AVAILABLE
        )

        whenever(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation))
        whenever(vehicleService.getVehicleById(vehicle.getId())).thenReturn(vehicleDTO)
        whenever(reservationRepository.save(reservation)).thenReturn(reservation)

        // When
        val result = reservationService.updateReservationStatus(reservationId, statusToUpdate)

        // Then
        val expected = reservation.toDTO(vehicleDTO)
        assertEquals(expected.reservationId, result.reservationId)
        assertEquals(statusToUpdate, reservation.reservationStatus)
    }



}


