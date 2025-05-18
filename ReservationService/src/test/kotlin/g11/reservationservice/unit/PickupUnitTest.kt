package g11.reservationservice.services

// ✅ GOOD: Kotlin-style imports
import g11.reservationservice.dtos.CarModelResponseDTO
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.any

import g11.reservationservice.dtos.ReservationResponseDTO
import g11.reservationservice.dtos.TransferDTO
import g11.reservationservice.dtos.VehicleResponseDTO
import g11.reservationservice.entities.Pickup
import g11.reservationservice.entities.Reservation
import g11.reservationservice.entities.Vehicle
import g11.reservationservice.entities.enumerations.ReservationStatus
import g11.reservationservice.entities.enumerations.TransferType
import g11.reservationservice.entities.enumerations.VehicleStatus
import g11.reservationservice.exceptions.TransferNotFoundException
import g11.reservationservice.repositories.DropoffRepository
import g11.reservationservice.repositories.PickupRepository
import g11.reservationservice.services.impl.TransferServiceImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class TransferServiceImplTest {


    @Mock
    lateinit var pickupRepository: PickupRepository

    @Mock
    lateinit var dropoffRepository: DropoffRepository

    @Mock
    lateinit var reservationService: ReservationService

    @InjectMocks
    lateinit var transferService: TransferServiceImpl
    @Test
    fun `deleteTransfer should delete pickup and unlink reservation`() {
        val pickupId = 100L
        val reservationId = 200L

        val reservation = mock<Reservation>()
        doReturn(reservationId).whenever(reservation).getId()

        val pickup = mock<Pickup>()
        doReturn(reservation).whenever(pickup).reservation

        whenever(pickupRepository.findById(pickupId)).thenReturn(Optional.of(pickup))

        transferService.deleteTransfer(TransferType.PICKUP, pickupId)

        verify(reservationService).removeTransferFromReservation(TransferType.PICKUP, reservationId)
    }


    @Test
    fun `deleteTransfer should throw if pickup not found`() {
        val pickupId = 123L

        whenever(pickupRepository.findById(pickupId)).thenReturn(Optional.empty())

        val exception = assertThrows(TransferNotFoundException::class.java) {
            transferService.deleteTransfer(TransferType.PICKUP, pickupId)
        }

        assertEquals("Pickup with ID $pickupId not found", exception.message)
    }
    @Test
    fun `getTransferById should return pickup TransferResponseDTO when pickup exists`() {
        val reservationId = 1L
        val vehicleId = 10L
        val transferTime = LocalDateTime.now()
        val location = "Airport"
        val staffId = 20L

        val pickup = TransferDTO(
            transferTime = transferTime,
            location = location,
            handledByStaffId = staffId
        )

        val carModel = CarModelResponseDTO(
            id = 1L,
            brand = "Toyota",
            model = "Yaris",
            modelYear = 2023,
            segment = "Hatchback",
            numberOfDoors = 5,
            seatingCapacity = 5,
            luggageCapacity = 300,
            category = "Economy",
            engineType = "Hybrid",
            transmissionType = "Automatic",
            drivetrain = "FWD",
            motorDisplacement = 1.5,
            features = mutableSetOf("Bluetooth"),
            rentalPricePerDay = 35.99f
        )

        val vehicle = VehicleResponseDTO(
            id = vehicleId,
            carModel = carModel,
            licensePlate = "XYZ-5678",
            vin = "2HGCM82633A004353",
            kilometersTravelled = 20000,
            vehicleStatus = VehicleStatus.AVAILABLE
        )

        val reservationDTO = ReservationResponseDTO(
            reservationId = reservationId,
            userId = 100L,
            vehicle = vehicle,
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(2),
            reservationStatus = ReservationStatus.CONFIRMED,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            totalPrice = 100f,
            cancellationDate = null,
            pickup = pickup,
            dropoff = null
        )

        whenever(reservationService.getReservationById(reservationId)).thenReturn(reservationDTO)

        // When
        val result = transferService.getTransferById(TransferType.PICKUP, reservationId)

        // Then
        assertEquals(reservationId, result.reservationId)
        assertEquals(vehicleId, result.vehicleId)
        assertEquals(transferTime, result.transferTime)
        assertEquals(location, result.location)
        assertEquals(staffId, result.handledByStaffId)
    }


    @Test
    fun `getAllTransfers should return only pickups when type is PICKUP`() {
        // Given
        val transferTime = LocalDateTime.now()
        val location = "Airport"
        val staffId = 42L
        val vehicleId = 10L

        val pickup = TransferDTO(
            transferTime = transferTime,
            location = location,
            handledByStaffId = staffId
        )

        val carModel = CarModelResponseDTO(
            id = 1L,
            brand = "Toyota",
            model = "Yaris",
            modelYear = 2023,
            segment = "Hatchback",
            numberOfDoors = 5,
            seatingCapacity = 5,
            luggageCapacity = 300,
            category = "Economy",
            engineType = "Hybrid",
            transmissionType = "Automatic",
            drivetrain = "FWD",
            motorDisplacement = 1.5,
            features = mutableSetOf("Bluetooth"),
            rentalPricePerDay = 35.99f
        )

        val vehicle = VehicleResponseDTO(
            id = vehicleId,
            carModel = carModel,
            licensePlate = "XYZ-5678",
            vin = "2HGCM82633A004353",
            kilometersTravelled = 20000,
            vehicleStatus = VehicleStatus.AVAILABLE
        )

        val reservationWithPickup = ReservationResponseDTO(
            reservationId = 1L,
            userId = 100L,
            vehicle = vehicle,
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(2),
            reservationStatus = ReservationStatus.CONFIRMED,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            totalPrice = 100f,
            cancellationDate = null,
            pickup = pickup,
            dropoff = null
        )

        val reservationWithoutPickup = reservationWithPickup.copy(
            reservationId = 2L,
            pickup = null
        )

        whenever(reservationService.getAllReservations()).thenReturn(listOf(reservationWithPickup, reservationWithoutPickup))

        // When
        val result = transferService.getAllTransfers(TransferType.PICKUP)

        // Then
        assertEquals(1, result.size)
        val dto = result.first()
        assertEquals(1L, dto.reservationId)
        assertEquals(vehicleId, dto.vehicleId)
        assertEquals(location, dto.location)
        assertEquals(transferTime, dto.transferTime)
        assertEquals(staffId, dto.handledByStaffId)
    }

    @Test
    fun `getTransferById should throw if pickup is null`() {
        // Given
        val reservationId = 1L

        val carModel = CarModelResponseDTO(
            id = 1L,
            brand = "Toyota",
            model = "Yaris",
            modelYear = 2023,
            segment = "Hatchback",
            numberOfDoors = 5,
            seatingCapacity = 5,
            luggageCapacity = 300,
            category = "Economy",
            engineType = "Hybrid",
            transmissionType = "Automatic",
            drivetrain = "FWD",
            motorDisplacement = 1.5,
            features = mutableSetOf("Bluetooth"),
            rentalPricePerDay = 35.99f
        )

        val vehicle = VehicleResponseDTO(
            id = 10L,
            carModel = carModel,
            licensePlate = "XYZ-5678",
            vin = "2HGCM82633A004353",
            kilometersTravelled = 20000,
            vehicleStatus = VehicleStatus.AVAILABLE
        )

        val reservationDTO = ReservationResponseDTO(
            reservationId = reservationId,
            userId = 100L,
            vehicle = vehicle,
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(2),
            reservationStatus = ReservationStatus.CONFIRMED,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            totalPrice = 100f,
            cancellationDate = null,
            pickup = null, // Key point
            dropoff = null
        )

        whenever(reservationService.getReservationById(reservationId)).thenReturn(reservationDTO)

        // When + Then
        val exception = assertThrows(TransferNotFoundException::class.java) {
            transferService.getTransferById(TransferType.PICKUP, reservationId)
        }

        assertEquals("PICKUP not available for reservation ID $reservationId", exception.message)
    }


}
