package g11.reservationservice.services
import g11.reservationservice.entities.Dropoff
import g11.reservationservice.entities.Reservation
import g11.reservationservice.entities.enumerations.TransferType
import org.mockito.kotlin.mock
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever
import org.mockito.kotlin.verify

import g11.reservationservice.dtos.*
import g11.reservationservice.entities.enumerations.*
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
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Optional
import kotlin.jvm.java

@ExtendWith(MockitoExtension::class)
class TransferServiceDropoffTest {

    @Mock
    lateinit var pickupRepository: PickupRepository

    @Mock
    lateinit var dropoffRepository: DropoffRepository

    @Mock
    lateinit var reservationService: ReservationService

    @InjectMocks
    lateinit var transferService: TransferServiceImpl

    @Test
    fun `getAllTransfers should return only dropoffs when type is DROPOFF`() {
        // Given
        val transferTime = LocalDateTime.now()
        val location = "Hotel"
        val staffId = 99L
        val vehicleId = 20L

        val dropoff = TransferDTO(
            transferTime = transferTime,
            location = location,
            handledByStaffId = staffId
        )

        val carModel = CarModelResponseDTO(
            id = 2L,
            brand = "Ford",
            model = "Focus",
            modelYear = 2021,
            segment = "Sedan",
            numberOfDoors = 4,
            seatingCapacity = 5,
            luggageCapacity = 400,
            category = "Midsize",
            engineType = "Diesel",
            transmissionType = "Manual",
            drivetrain = "FWD",
            motorDisplacement = 2.0,
            features = mutableSetOf("GPS", "Heated Seats"),
            rentalPricePerDay = 55.00f
        )

        val vehicle = VehicleResponseDTO(
            id = vehicleId,
            carModel = carModel,
            licensePlate = "DEF-9012",
            vin = "3HGCM82633A004354",
            kilometersTravelled = 30000,
            vehicleStatus = VehicleStatus.AVAILABLE
        )

        val reservationWithDropoff = ReservationResponseDTO(
            reservationId = 3L,
            userId = 200L,
            vehicle = vehicle,
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(4),
            reservationStatus = ReservationStatus.CONFIRMED,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            totalPrice = 250f,
            cancellationDate = null,
            pickup = null,
            dropoff = dropoff
        )

        val reservationWithoutDropoff = reservationWithDropoff.copy(
            reservationId = 4L,
            dropoff = null
        )

        whenever(reservationService.getAllReservations()).thenReturn(
            listOf(reservationWithDropoff, reservationWithoutDropoff)
        )

        // When
        val result = transferService.getAllTransfers(TransferType.DROPOFF)

        // Then
        assertEquals(1, result.size)
        val dto = result.first()
        assertEquals(3L, dto.reservationId)
        assertEquals(vehicleId, dto.vehicleId)
        assertEquals(location, dto.location)
        assertEquals(transferTime, dto.transferTime)
        assertEquals(staffId, dto.handledByStaffId)
    }

    @Test
    fun `deleteTransfer should delete dropoff and unlink reservation`() {
        val dropoffId = 300L
        val reservationId = 400L

        val reservation = mock<Reservation>()
        doReturn(reservationId).whenever(reservation).getId()

        val dropoff = mock<Dropoff>()
        doReturn(reservation).whenever(dropoff).reservation

        whenever(dropoffRepository.findById(dropoffId)).thenReturn(Optional.of(dropoff))

        transferService.deleteTransfer(TransferType.DROPOFF, dropoffId)

        verify(reservationService).removeTransferFromReservation(TransferType.DROPOFF, reservationId)
    }
    @Test
    fun `deleteTransfer should throw if dropoff not found`() {
        val dropoffId = 999L

        whenever(dropoffRepository.findById(dropoffId)).thenReturn(Optional.empty())

        // When + Then
        val exception = assertThrows(TransferNotFoundException::class.java) {
            transferService.deleteTransfer(TransferType.DROPOFF, dropoffId)
        }

        assertEquals("Dropoff with ID $dropoffId not found", exception.message)
    }

    @Test
    fun `getTransferById should return dropoff TransferResponseDTO when dropoff exists`() {
        // Given
        val reservationId = 5L
        val vehicleId = 101L
        val transferTime = LocalDateTime.now()
        val location = "Hotel"
        val staffId = 33L

        val dropoff = TransferDTO(
            transferTime = transferTime,
            location = location,
            handledByStaffId = staffId
        )

        val carModel = CarModelResponseDTO(
            id = 9L,
            brand = "Peugeot",
            model = "208",
            modelYear = 2022,
            segment = "Hatchback",
            numberOfDoors = 5,
            seatingCapacity = 5,
            luggageCapacity = 300,
            category = "Economy",
            engineType = "Petrol",
            transmissionType = "Manual",
            drivetrain = "FWD",
            motorDisplacement = 1.2,
            features = mutableSetOf("Bluetooth", "Air Conditioning"),
            rentalPricePerDay = 30.0f
        )

        val vehicle = VehicleResponseDTO(
            id = vehicleId,
            carModel = carModel,
            licensePlate = "LMN-4567",
            vin = "WBA3A5C58CF123456",
            kilometersTravelled = 15000,
            vehicleStatus = VehicleStatus.AVAILABLE
        )

        val reservationDTO = ReservationResponseDTO(
            reservationId = reservationId,
            userId = 55L,
            vehicle = vehicle,
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(2),
            reservationStatus = ReservationStatus.CONFIRMED,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            totalPrice = 180.0f,
            cancellationDate = null,
            pickup = null,
            dropoff = dropoff
        )

        whenever(reservationService.getReservationById(reservationId)).thenReturn(reservationDTO)

        // When
        val result = transferService.getTransferById(TransferType.DROPOFF, reservationId)

        // Then
        assertEquals(reservationId, result.reservationId)
        assertEquals(vehicleId, result.vehicleId)
        assertEquals(transferTime, result.transferTime)
        assertEquals(location, result.location)
        assertEquals(staffId, result.handledByStaffId)
    }

    @Test
    fun `getTransferById should throw if dropoff is null`() {
        // Given
        val reservationId = 5L

        val carModel = CarModelResponseDTO(
            id = 9L,
            brand = "Peugeot",
            model = "208",
            modelYear = 2022,
            segment = "Hatchback",
            numberOfDoors = 5,
            seatingCapacity = 5,
            luggageCapacity = 300,
            category = "Economy",
            engineType = "Petrol",
            transmissionType = "Manual",
            drivetrain = "FWD",
            motorDisplacement = 1.2,
            features = mutableSetOf("Bluetooth", "Air Conditioning"),
            rentalPricePerDay = 30.0f
        )

        val vehicle = VehicleResponseDTO(
            id = 101L,
            carModel = carModel,
            licensePlate = "LMN-4567",
            vin = "WBA3A5C58CF123456",
            kilometersTravelled = 15000,
            vehicleStatus = VehicleStatus.AVAILABLE
        )

        val reservationDTO = ReservationResponseDTO(
            reservationId = reservationId,
            userId = 55L,
            vehicle = vehicle,
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(2),
            reservationStatus = ReservationStatus.CONFIRMED,
            createdAt = LocalDateTime.now(),
            updatedAt = null,
            totalPrice = 180.0f,
            cancellationDate = null,
            pickup = null,
            dropoff = null // <--- key point
        )

        whenever(reservationService.getReservationById(reservationId)).thenReturn(reservationDTO)

        // When + Then
        val exception = assertThrows(TransferNotFoundException::class.java) {
            transferService.getTransferById(TransferType.DROPOFF, reservationId)
        }

        assertEquals("DROPOFF not available for reservation ID $reservationId", exception.message)
    }


}
