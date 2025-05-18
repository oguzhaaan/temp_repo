package g11.reservationservice.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import g11.reservationservice.dtos.CustomerProfileDTO
import g11.reservationservice.dtos.ReservationTimeIntervalDTO
import g11.reservationservice.dtos.ReservationUpdateDTO
import g11.reservationservice.dtos.TransferDTO
import g11.reservationservice.dtos.UserDTO
import g11.reservationservice.entities.*
import g11.reservationservice.entities.enumerations.*
import g11.reservationservice.exceptions.MissingCustomerProfileException
import g11.reservationservice.repositories.*
import g11.reservationservice.services.ReservationService
import g11.reservationservice.services.UserClient
import junit.framework.TestCase.assertEquals
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.MediaType
import org.springframework.test.annotation.Rollback
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import org.mockito.Mockito.mock
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Role
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import java.time.temporal.ChronoUnit


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
@Import(TestConfig::class)  // Import the TestConfig to use the mock UserClient
class ReservationControllerIntegrationTest2 {

    @Autowired
    private lateinit var reservationService: ReservationService

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var segmentRepository: SegmentRepository

    @Autowired
    lateinit var reservationRepository: ReservationRepository

    @Autowired
    lateinit var brandRepository: BrandRepository

    @Autowired
    lateinit var brandModelRepository: BrandModelRepository

    @Autowired
    lateinit var carModelRepository: CarModelRepository

    @Autowired
    lateinit var vehicleRepository: VehicleRepository

    @Autowired
    private lateinit var userClient: UserClient


    @TestConfiguration
    class TestConfig {
        @Bean
        @Primary  // Ensures this mock is the one used in the test context
        fun userClient(): UserClient = mock(UserClient::class.java)
    }

    @Test
    fun `createReservation should return 201 Created`() {
        // Mock RestClient behavior
        val mockUser = UserDTO(
            id = 1L,
            role = "CUSTOMER",
            customerProfile = CustomerProfileDTO(drivingLicenseExpiry = LocalDate.now().plusYears(1))
        )

        `when`(userClient.getUserById(1L)).thenReturn(mockUser)

        // Create required entities
        val brand = brandRepository.save(Brand("Mazda"))
        val brandModel = brandModelRepository.save(BrandModel(brand, "CX-5"))
        val carModel = carModelRepository.save(
            CarModel(
                brandModel = brandModel,
                modelYear = 2023,
                segment = Segment("SUV"),
                numberOfDoors = 5,
                seatingCapacity = 5,
                luggageCapacity = 2,
                category = Category.ECONOMY,
                engineType = EngineType.PETROL,
                transmissionType = TransmissionType.AUTOMATIC,
                drivetrain = Drivetrain.AWD,
                motorDisplacement = 2.0,
                features = mutableSetOf(),
                rentalPricePerDay = 85f
            )
        )
        val vehicle = vehicleRepository.save(
            Vehicle(
                carModel = carModel,
                licensePlate = "ABC-1234",
                vin = "VIN123456789",
                kilometersTravelled = 20000,
                vehicleStatus = VehicleStatus.AVAILABLE
            )
        )

        val reservationInterval = ReservationTimeIntervalDTO(
            startDate = LocalDate.now().plusDays(1),
            endDate = LocalDate.now().plusDays(3)
        )

        val jsonBody = objectMapper.writeValueAsString(reservationInterval)

        mockMvc.post("/api/v1/reservations/users/1/cars/${carModel.getId()}") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = jsonBody
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.userId", equalTo(1))
            jsonPath("$.vehicle.id", equalTo(vehicle.getId()!!.toInt()))
            jsonPath("$.startDate", equalTo(reservationInterval.startDate.toString()))
            jsonPath("$.endDate", equalTo(reservationInterval.endDate.toString()))
            jsonPath("$.reservationStatus", equalTo("WAITING_FOR_PAYMENT"))
        }
    }


    @Test
    fun `createReservation should return 400 if driving license is expired`() {
        // Mock user with expired license
        val mockUser = UserDTO(
            id = 1L,
            role = "CUSTOMER",
            customerProfile = CustomerProfileDTO(drivingLicenseExpiry = LocalDate.now().minusDays(1))
        )
        `when`(userClient.getUserById(1L)).thenReturn(mockUser)

        // Create required entities
        val brand = brandRepository.save(Brand("Mazda"))
        val brandModel = brandModelRepository.save(BrandModel(brand, "CX-5"))
        val carModel = carModelRepository.save(
            CarModel(
                brandModel = brandModel,
                modelYear = 2023,
                segment = Segment("SUV"),
                numberOfDoors = 5,
                seatingCapacity = 5,
                luggageCapacity = 2,
                category = Category.ECONOMY,
                engineType = EngineType.PETROL,
                transmissionType = TransmissionType.AUTOMATIC,
                drivetrain = Drivetrain.AWD,
                motorDisplacement = 2.0,
                features = mutableSetOf(),
                rentalPricePerDay = 85f
            )
        )
        vehicleRepository.save(
            Vehicle(
                carModel = carModel,
                licensePlate = "XYZ-0000",
                vin = "VIN987654321",
                kilometersTravelled = 25000,
                vehicleStatus = VehicleStatus.AVAILABLE
            )
        )

        val reservationInterval = ReservationTimeIntervalDTO(
            startDate = LocalDate.now().plusDays(1),
            endDate = LocalDate.now().plusDays(3)  // After the license expiry
        )
        val jsonBody = objectMapper.writeValueAsString(reservationInterval)

        mockMvc.post("/api/v1/reservations/users/1/cars/${carModel.getId()}") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = jsonBody
        }.andExpect {
            status { isBadRequest() }
            content { contentType("application/problem+json") }
            jsonPath("$.detail", equalTo("Driving license must be valid for the reservation period."))
        }
    }


    @Test
    @Transactional
    @Rollback
    fun `updateReservationInfo should return 200 OK and updated reservation`() {

        // --- Prepare base data ---
        val brand = brandRepository.save(Brand("Honda"))
        val brandModel = brandModelRepository.save(BrandModel(brand, "Civic"))

        // Prepare car models
        val carModel1 = carModelRepository.save(
            CarModel(
                brandModel = brandModel,
                modelYear = 2022,
                segment = Segment("Van"),
                numberOfDoors = 4,
                seatingCapacity = 5,
                luggageCapacity = 2,
                category = Category.ECONOMY,
                engineType = EngineType.PETROL,
                transmissionType = TransmissionType.AUTOMATIC,
                drivetrain = Drivetrain.FWD,
                motorDisplacement = 1.6,
                features = mutableSetOf(),
                rentalPricePerDay = 70f
            )
        )

        val carModel2 = carModelRepository.save(
            CarModel(
                brandModel = brandModel,
                modelYear = 2023,
                segment = Segment("Van"),
                numberOfDoors = 4,
                seatingCapacity = 5,
                luggageCapacity = 2,
                category = Category.ECONOMY,
                engineType = EngineType.HYBRID,
                transmissionType = TransmissionType.AUTOMATIC,
                drivetrain = Drivetrain.FWD,
                motorDisplacement = 1.8,
                features = mutableSetOf(),
                rentalPricePerDay = 80f
            )
        )

        // Prepare vehicle for carModel1
        val vehicle1 = vehicleRepository.save(
            Vehicle(
                carModel = carModel1,
                licensePlate = "HH-567-GH",
                vin = "VIN000999888",
                kilometersTravelled = 15000,
                vehicleStatus = VehicleStatus.AVAILABLE
            )
        )

        // Prepare vehicle for carModel2
        val vehicle2 = vehicleRepository.save(
            Vehicle(
                carModel = carModel2,
                licensePlate = "HH-568-GH",
                vin = "VIN111999888",
                kilometersTravelled = 5000,
                vehicleStatus = VehicleStatus.AVAILABLE
            )
        )

        // Mock RestClient behavior
        val mockUser = UserDTO(
            id = 1L,
            role = "CUSTOMER",
            customerProfile = CustomerProfileDTO(drivingLicenseExpiry = LocalDate.now().plusYears(1))
        )

        `when`(userClient.getUserById(1L)).thenReturn(mockUser)

        // Create initial reservation with vehicle1
        val reservation = reservationRepository.save(
            Reservation(
                userId = 1L,
                vehicle = vehicle1,  // Initially linked with vehicle1
                startDate = LocalDate.now().plusDays(2),
                endDate = LocalDate.now().plusDays(4),
                reservationStatus = ReservationStatus.CONFIRMED,
                createdAt = LocalDateTime.now(),
                totalPrice = 210f
            )
        )

        // --- Create update DTO to switch to carModel2 (with corresponding vehicle2) ---
        val updateDTO = ReservationUpdateDTO(
            carModelId = carModel2.getId(),  // Switching to carModel2
            reservationTimeInterval = ReservationTimeIntervalDTO(
                startDate = LocalDate.now().plusDays(3),
                endDate = LocalDate.now().plusDays(5)
            )
        )

        val jsonBody = objectMapper.writeValueAsString(updateDTO)

        // --- Call endpoint ---
        mockMvc.put("/api/v1/reservations/${reservation.getId()}") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = jsonBody
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.reservationId", equalTo(reservation.getId().toInt()))
            jsonPath("$.vehicle.carModel.modelYear", equalTo(2023))  // Expect the updated model year (2023)
            jsonPath("$.vehicle.id", equalTo(vehicle2.getId()!!.toInt()))  // Expect the updated vehicle id (vehicle2)
            jsonPath("$.startDate", equalTo(updateDTO.reservationTimeInterval.startDate.toString()))
            jsonPath("$.endDate", equalTo(updateDTO.reservationTimeInterval.endDate.toString()))
            jsonPath("$.reservationStatus", equalTo("CONFIRMED"))
        }
    }


    @Test
    @Transactional
    @Rollback
    fun `updateReservationInfo should return 404 if vehicle is not available`() {

        // --- Prepare base data ---
        val brand = brandRepository.save(Brand("Honda"))
        val brandModel = brandModelRepository.save(BrandModel(brand, "Civic"))

        // Prepare car models
        val carModel1 = carModelRepository.save(
            CarModel(
                brandModel = brandModel,
                modelYear = 2022,
                segment = Segment("Van"),
                numberOfDoors = 4,
                seatingCapacity = 5,
                luggageCapacity = 2,
                category = Category.ECONOMY,
                engineType = EngineType.PETROL,
                transmissionType = TransmissionType.AUTOMATIC,
                drivetrain = Drivetrain.FWD,
                motorDisplacement = 1.6,
                features = mutableSetOf(),
                rentalPricePerDay = 70f
            )
        )

        val carModel2 = carModelRepository.save(
            CarModel(
                brandModel = brandModel,
                modelYear = 2023,
                segment = Segment("Van"),
                numberOfDoors = 4,
                seatingCapacity = 5,
                luggageCapacity = 2,
                category = Category.ECONOMY,
                engineType = EngineType.HYBRID,
                transmissionType = TransmissionType.AUTOMATIC,
                drivetrain = Drivetrain.FWD,
                motorDisplacement = 1.8,
                features = mutableSetOf(),
                rentalPricePerDay = 80f
            )
        )

        // Prepare vehicle for carModel1
        val vehicle1 = vehicleRepository.save(
            Vehicle(
                carModel = carModel1,
                licensePlate = "HH-567-GH",
                vin = "VIN000999888",
                kilometersTravelled = 15000,
                vehicleStatus = VehicleStatus.AVAILABLE
            )
        )


        // Mock RestClient behavior
        val mockUser = UserDTO(
            id = 1L,
            role = "CUSTOMER",
            customerProfile = CustomerProfileDTO(drivingLicenseExpiry = LocalDate.now().plusYears(1))
        )

        `when`(userClient.getUserById(1L)).thenReturn(mockUser)

        // Create initial reservation with vehicle1
        val reservation = reservationRepository.save(
            Reservation(
                userId = 1L,
                vehicle = vehicle1,  // Initially linked with vehicle1
                startDate = LocalDate.now().plusDays(2),
                endDate = LocalDate.now().plusDays(4),
                reservationStatus = ReservationStatus.CONFIRMED,
                createdAt = LocalDateTime.now(),
                totalPrice = 210f
            )
        )

        // --- Create update DTO to switch to carModel2 (with corresponding vehicle2) ---
        val updateDTO = ReservationUpdateDTO(
            carModelId = carModel2.getId(),  // Switching to carModel2
            reservationTimeInterval = ReservationTimeIntervalDTO(
                startDate = LocalDate.now().plusDays(3),
                endDate = LocalDate.now().plusDays(5)
            )
        )

        val jsonBody = objectMapper.writeValueAsString(updateDTO)


        // --- Call endpoint and expect 404 due to no available vehicle ---
        mockMvc.put("/api/v1/reservations/${reservation.getId()}") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = jsonBody
        }.andExpect {
            status { isNotFound() }
            content { contentType("application/problem+json") }
            jsonPath("$.detail", equalTo("No available vehicle..."))
        }

    }

    @Test
    @Transactional
    @Rollback
    fun `updateReservationStatus should return 200 OK and updated status`() {
        // --- Prepare necessary data ---
        val brand = brandRepository.save(Brand("Fiat"))
        val brandModel = brandModelRepository.save(BrandModel(brand, "Panda"))
        val segment = segmentRepository.save(Segment("CITY"))

        val carModel = carModelRepository.save(
            CarModel(
                brandModel = brandModel,
                modelYear = 2021,
                segment = segment,
                numberOfDoors = 5,
                seatingCapacity = 4,
                luggageCapacity = 1,
                category = Category.ECONOMY,
                engineType = EngineType.PETROL,
                transmissionType = TransmissionType.MANUAL,
                drivetrain = Drivetrain.FWD,
                motorDisplacement = 1.2,
                features = mutableSetOf(),
                rentalPricePerDay = 55f
            )
        )

        val vehicle = vehicleRepository.save(
            Vehicle(
                carModel = carModel,
                licensePlate = "CD-1234-EF",
                vin = "VIN555123ABC",
                kilometersTravelled = 30000,
                vehicleStatus = VehicleStatus.AVAILABLE
            )
        )

        val reservation = reservationRepository.save(
            Reservation(
                userId = 1L,
                vehicle = vehicle,
                startDate = LocalDate.now().plusDays(1),
                endDate = LocalDate.now().plusDays(3),
                reservationStatus = ReservationStatus.WAITING_FOR_PAYMENT,
                createdAt = LocalDateTime.now(),
                totalPrice = 165f
            )
        )

        // --- Perform status update (e.g., cancel the reservation) ---
        val newStatus = ReservationStatus.CANCELLED.name

        mockMvc.put("/api/v1/reservations/${reservation.getId()}/status/$newStatus") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.reservationId", equalTo(reservation.getId()!!.toInt()))
            jsonPath("$.reservationStatus", equalTo(newStatus))
        }
    }

    @Test
    @Transactional
    @Rollback
    fun `getReservationById should return 200 OK with reservation`() {
        // --- Setup test data ---
        val brand = brandRepository.save(Brand("Renault"))
        val brandModel = brandModelRepository.save(BrandModel(brand, "Clio"))
        val segment = segmentRepository.save(Segment("HATCHBACK"))

        val carModel = carModelRepository.save(
            CarModel(
                brandModel = brandModel,
                modelYear = 2020,
                segment = segment,
                numberOfDoors = 5,
                seatingCapacity = 5,
                luggageCapacity = 2,
                category = Category.ECONOMY,
                engineType = EngineType.PETROL,
                transmissionType = TransmissionType.MANUAL,
                drivetrain = Drivetrain.FWD,
                motorDisplacement = 1.4,
                features = mutableSetOf(),
                rentalPricePerDay = 60f
            )
        )

        val vehicle = vehicleRepository.save(
            Vehicle(
                carModel = carModel,
                licensePlate = "XY-4321-ZW",
                vin = "VIN987654321",
                kilometersTravelled = 45000,
                vehicleStatus = VehicleStatus.AVAILABLE
            )
        )

        val reservation = reservationRepository.save(
            Reservation(
                userId = 1L,
                vehicle = vehicle,
                startDate = LocalDate.now().plusDays(1),
                endDate = LocalDate.now().plusDays(3),
                reservationStatus = ReservationStatus.CONFIRMED,
                createdAt = LocalDateTime.now(),
                totalPrice = 180f
            )
        )

        // --- Perform GET by ID ---
        mockMvc.get("/api/v1/reservations/${reservation.getId()}") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.reservationId", equalTo(reservation.getId()!!.toInt()))
            jsonPath("$.userId", equalTo(1))
            jsonPath("$.reservationStatus", equalTo("CONFIRMED"))
        }
    }

    @Test
    fun `getReservationById should return 404 when reservation does not exist`() {
        val nonExistentId = 99999L

        mockMvc.get("/api/v1/reservations/$nonExistentId") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }
    }


//    @Test
//    fun `getAllReservationsByUserId should return 200 OK with all reservations`() {
//        val mockUser = UserDTO(
//            id = 2L,
//            role = "CUSTOMER",
//            customerProfile = CustomerProfileDTO(drivingLicenseExpiry = LocalDate.now().plusYears(1))
//        )
//
//        `when`(userClient.getUserById(2L)).thenReturn(mockUser)
//
//        val userId = 2L
//        val brand = brandRepository.save(Brand("Citroen"))
//        val brandModel = brandModelRepository.save(BrandModel(brand, "C3"))
//        val segment = segmentRepository.save(Segment("COMPACT"))
//
//        val carModel = carModelRepository.save(
//            CarModel(
//                brandModel = brandModel,
//                modelYear = 2023,
//                segment = segment,
//                numberOfDoors = 5,
//                seatingCapacity = 5,
//                luggageCapacity = 3,
//                category = Category.ECONOMY,
//                engineType = EngineType.DIESEL,
//                transmissionType = TransmissionType.AUTOMATIC,
//                drivetrain = Drivetrain.FWD,
//                motorDisplacement = 1.5,
//                features = mutableSetOf(),
//                rentalPricePerDay = 75f
//            )
//        )
//
//        val vehicle = vehicleRepository.save(
//            Vehicle(
//                carModel = carModel,
//                licensePlate = "ZZ-987-YY",
//                vin = "VIN9988776655",
//                kilometersTravelled = 12000,
//                vehicleStatus = VehicleStatus.AVAILABLE
//            )
//        )
//
//        reservationRepository.save(
//            Reservation(
//                userId = userId,
//                vehicle = vehicle,
//                startDate = LocalDate.now().plusDays(1),
//                endDate = LocalDate.now().plusDays(3),
//                reservationStatus = ReservationStatus.CONFIRMED,
//                createdAt = LocalDateTime.now(),
//                totalPrice = 225f
//            )
//        )
//
//        reservationRepository.save(
//            Reservation(
//                userId = userId,
//                vehicle = vehicle,
//                startDate = LocalDate.now().plusDays(4),
//                endDate = LocalDate.now().plusDays(6),
//                reservationStatus = ReservationStatus.CONFIRMED,
//                createdAt = LocalDateTime.now(),
//                totalPrice = 250f
//            )
//        )
//
//        mockMvc.get("/api/v1/reservations/users/$userId") {
//            accept = MediaType.APPLICATION_JSON
//        }.andExpect {
//            status { isOk() }
//            content { contentType(MediaType.APPLICATION_JSON) }
//            jsonPath("$[0].userId", equalTo(userId.toInt()))
//            jsonPath("$[0].vehicle.licensePlate", equalTo("ZZ-987-YY"))
//            jsonPath("$[0].reservationStatus", equalTo("CONFIRMED"))
//            jsonPath("$[0].totalPrice", equalTo(225.0))  // Use double here
//
//            jsonPath("$[1].userId", equalTo(userId.toInt()))
//            jsonPath("$[1].vehicle.licensePlate", equalTo("ZZ-987-YY"))
//            jsonPath("$[1].totalPrice", equalTo(250.0))
//        }
//    }


    @Test
    @Transactional
    @Rollback
    fun `deleteReservation should return 204 when reservation is deleted`() {
        // --- Setup test data ---
        val brand = brandRepository.save(Brand("Mazda"))
        val brandModel = brandModelRepository.save(BrandModel(brand, "CX-5"))
        val segment = segmentRepository.save(Segment("SUV"))

        val carModel = carModelRepository.save(
            CarModel(
                brandModel = brandModel,
                modelYear = 2022,
                segment = segment,
                numberOfDoors = 5,
                seatingCapacity = 5,
                luggageCapacity = 3,
                category = Category.ECONOMY,
                engineType = EngineType.PETROL,
                transmissionType = TransmissionType.AUTOMATIC,
                drivetrain = Drivetrain.AWD,
                motorDisplacement = 2.0,
                features = mutableSetOf(),
                rentalPricePerDay = 90f
            )
        )

        val vehicle = vehicleRepository.save(
            Vehicle(
                carModel = carModel,
                licensePlate = "DL-456-ZK",
                vin = "VIN456321789",
                kilometersTravelled = 25000,
                vehicleStatus = VehicleStatus.AVAILABLE
            )
        )

        val reservation = reservationRepository.save(
            Reservation(
                userId = 1L,
                vehicle = vehicle,
                startDate = LocalDate.now().plusDays(2),
                endDate = LocalDate.now().plusDays(4),
                reservationStatus = ReservationStatus.COMPLETED,
                createdAt = LocalDateTime.now(),
                totalPrice = 270f
            )
        )

        // --- Perform DELETE request ---
        mockMvc.delete("/api/v1/reservations/${reservation.getId()}") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNoContent() }
        }

    }

    @Test
    fun `deleteReservation should return 404 when reservation does not exist`() {
        val nonexistentId = 999999L

        mockMvc.delete("/api/v1/reservations/$nonexistentId") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }
    }

    // InvalidReservationStatusException

    @Test
    @Transactional
    @Rollback
    fun `deleteReservation with status ONGOING should return 400`() {
        // --- Setup test data ---
        val brand = brandRepository.save(Brand("Toyota"))
        val brandModel = brandModelRepository.save(BrandModel(brand, "Corolla"))
        val segment = segmentRepository.save(Segment("SEDAN"))

        val carModel = carModelRepository.save(
            CarModel(
                brandModel = brandModel,
                modelYear = 2023,
                segment = segment,
                numberOfDoors = 4,
                seatingCapacity = 5,
                luggageCapacity = 2,
                category = Category.ECONOMY,
                engineType = EngineType.PETROL,
                transmissionType = TransmissionType.AUTOMATIC,
                drivetrain = Drivetrain.FWD,
                motorDisplacement = 1.8,
                features = mutableSetOf(),
                rentalPricePerDay = 80f
            )
        )

        val vehicle = vehicleRepository.save(
            Vehicle(
                carModel = carModel,
                licensePlate = "GH-123-JK",
                vin = "VIN123456789",
                kilometersTravelled = 10000,
                vehicleStatus = VehicleStatus.AVAILABLE
            )
        )

        val reservation = reservationRepository.save(
            Reservation(
                userId = 1L,
                vehicle = vehicle,
                startDate = LocalDate.now().plusDays(1),
                endDate = LocalDate.now().plusDays(3),
                reservationStatus = ReservationStatus.ONGOING,
                createdAt = LocalDateTime.now(),
                totalPrice = 240f
            )
        )

        // --- Perform DELETE request ---
        mockMvc.delete("/api/v1/reservations/${reservation.getId()}") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `updateTransfer should return 400 when handledByStaffId user has no customer profile`() {
        // Given
        val transferDTO = TransferDTO(
            transferTime = LocalDateTime.now().plusHours(1),
            location = "Airport",
            handledByStaffId = 101L
        )

        val userDto = UserDTO(id = 101L, role = "CUSTOMER", customerProfile = null)
        whenever(userClient.getUserById(101L)).thenReturn(userDto)


        MockitoHelper.anyObject<TransferDTO>()

        Mockito.`when`(reservationService.updateTransfer(
            ArgumentMatchers.eq(TransferType.PICKUP),
            ArgumentMatchers.eq(1L),
            MockitoHelper.anyObject()
        )).thenThrow(MissingCustomerProfileException())


        // When / Then
        mockMvc.put("/api/v1/reservations/1/transfer/PICKUP") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(transferDTO)
        }.andExpect {
            status { isBadRequest() }
            content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
            jsonPath("$.detail") { value("Customer profile is missing") }
        }
    }
}


@TestConfiguration
class TestConfig {
    @Bean
    @Primary  // Ensure this mock overrides the default UserClient bean in the test context
    fun userClient(): UserClient = mock(UserClient::class.java)
}

object MockitoHelper {
    fun <T> anyObject(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> uninitialized(): T = null as T
}