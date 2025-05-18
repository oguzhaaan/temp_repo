package g11.reservationservice

import com.jayway.jsonpath.JsonPath
import g11.reservationservice.dtos.*
import g11.reservationservice.entities.enumerations.*
import jakarta.transaction.Transactional
import org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.Rollback
import org.hamcrest.Matchers.*
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.*
import kotlin.test.assertNotNull

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class ReservationControllersApplicationTests : IntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `getAllbrands should return a list of brands`() {
        mockMvc.get("/api/v1/brands") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content {
                jsonPath("$") {
                    isNotEmpty()
                    isArray()
                    value(hasSize<Int>(13))
                    contains("Toyota")
                }
            }
        }
    }

    @Test
    fun `get Models by brand should return a list of brands`() {
        mockMvc.get("/api/v1/brandModels/Toyota") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content {
                jsonPath("$") {
                    isNotEmpty()
                    isArray()
                    value(hasSize<Int>(2))
                    contains("Yaris")
                    contains("Corolla")
                }
            }
        }
    }

    @Test
    fun `get All Brand Models should return a list of brands`() {
        mockMvc.get("/api/v1/brandModels") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content {
                jsonPath("$.*") {
                    isNotEmpty()
                    value(hasSize<Int>(13))
                }
                jsonPath("$.Kia") {
                    contains("Rio", "Ceed")
                }
            }
        }
    }

    @Test
    fun `get All Car Models should return a list of models`() {
        mockMvc.get("/api/v1/models") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content {
                jsonPath("$") {
                    isNotEmpty()
                    isArray()
                    value(hasSize<Int>(10))
                }
                jsonPath("$[0].brand") {
                    value("Fiat")
                }
            }
        }
    }

    @Test
    fun `get  Car Model by id should return a  model`() {
        mockMvc.get("/api/v1/models/1") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content {
                jsonPath("$.brand") {
                    isNotEmpty()
                    value("Fiat")
                }
            }
        }
    }

    @Test
    @Transactional
    @Rollback
    fun `create a new Car Model with id `() {

        val brand = "Fiat"
        val model = "500XL"
        val modelYear = 2001
        val segment = "Compact"
        val numberOfDoors = 4
        val seatingCapacity = 5
        val luggageCapacity = 400

        val mockRequestDTO = CarModelRequestDTO(
            brand,
            model,
            modelYear,
            segment,
            numberOfDoors,
            seatingCapacity,
            luggageCapacity,
            Category.ECONOMY,
            EngineType.HYBRID,
            TransmissionType.MANUAL,
            Drivetrain.FWD,
            motorDisplacement = 1.8,
            rentalPricePerDay = 50f
        )

        val requestBody = """
            {
    "brand": "${mockRequestDTO.brand}",
    "model": "${mockRequestDTO.brand}",
    "modelYear": ${mockRequestDTO.modelYear},
    "segment": "${mockRequestDTO.segment}",
    "numberOfDoors": ${mockRequestDTO.numberOfDoors},
     "seatingCapacity": ${mockRequestDTO.seatingCapacity},
    "luggageCapacity": ${mockRequestDTO.luggageCapacity},
     "category": "${mockRequestDTO.category}",
     "engineType": "${mockRequestDTO.engineType}",
     "transmissionType": "${mockRequestDTO.transmissionType}",
  "drivetrain": "${mockRequestDTO.drivetrain}",
  "motorDisplacement": ${mockRequestDTO.motorDisplacement},
  "features": [
    "Air Conditioning"
  ],
  "rentalPricePerDay": ${mockRequestDTO.rentalPricePerDay}
}
        """.trimIndent()


        mockMvc.post("/api/v1/models") {
            accept = MediaType.APPLICATION_JSON
            content = requestBody
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            jsonPath("$.modelYear") { value(mockRequestDTO.modelYear) }
            jsonPath("$.rentalPricePerDay") { value(mockRequestDTO.rentalPricePerDay) }
        }
    }

    @Test
    fun `create a new Car Model with id should return error bad request `() {

        val requestBody = """
            {
  "brand": "",
  "model": "",
  "modelYear": 1886,
  "segment": "",
  "numberOfDoors": 2,
  "seatingCapacity": 2,
  "luggageCapacity": 0,
  "category": "ECONOMY",
  "engineType": "PETROL",
  "transmissionType": "MANUAL",
  "drivetrain": "FWD",
  "motorDisplacement": 0,
  "features": [
    "string"
  ],
  "rentalPricePerDay": 0
}
        """.trimIndent()


        mockMvc.post("/api/v1/models") {
            accept = MediaType.APPLICATION_JSON
            content = requestBody
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
        }
    }


    @Test
    fun `get Car Model by id should return a not found error if the id is not valid`() {
        mockMvc.get("/api/v1/models/999") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    @Transactional
    @Rollback
    fun `delete Car Model by id should delete a car model`() {
        mockMvc.delete("/api/v1/models/10") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNoContent() }
        }
    }


    @Test
    fun `get Vehicle by id should return a vehicle`() {
        mockMvc.get("/api/v1/vehicles/1") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content {
                jsonPath("licensePlate") {
                    isNotEmpty()
                    value("FIAT500-001")
                }
            }
        }
    }


    @Test
    fun `get Vehicle by id should return 404 if id is invalid`() {
        mockMvc.get("/api/v1/vehicles/999") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `get AllVehicles should return a list of vehicles`() {
        mockMvc.get("/api/v1/vehicles") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content {
                jsonPath("$") {
                    isNotEmpty()
                    isArray()
                    value(hasSize<Int>(10))
                }
            }
        }
    }

    @Test
    @Transactional
    @Rollback
    fun `create Vehicle should return 400 if bad request parameters are sent`() {
        val vehicleRequestDTO = VehicleRequestDTO(
            3,
            "",
            "1HGBH41UU109186",
            15000,
            vehicleStatus = VehicleStatus.AVAILABLE
        )

        val requestBody = """
            {
                "carModelId": 3, 
                "licensePlate": "${vehicleRequestDTO.licensePlate}", 
                "vin": "${vehicleRequestDTO.vin}", 
                "kilometersTravelled": "${vehicleRequestDTO.kilometersTravelled}", 
                "vehicleStatus": "${vehicleRequestDTO.vehicleStatus}" 
            }
        """.trimIndent()

        mockMvc.post("/api/v1/vehicles") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody
        }.andExpect {
            status { isBadRequest() }
        }
    }


    @Test
    @Transactional
    @Rollback
    fun `create Vehicle should return a new created vehicle`() {
        val vehicleRequestDTO = VehicleRequestDTO(
            3,
            "FK200LB2",
            "1HGBH41UU109186",
            15000,
            vehicleStatus = VehicleStatus.AVAILABLE
        )

        val requestBody = """
            {
                "carModelId": 3, 
                "licensePlate": "${vehicleRequestDTO.licensePlate}", 
                "vin": "${vehicleRequestDTO.vin}", 
                "kilometersTravelled": "${vehicleRequestDTO.kilometersTravelled}", 
                "vehicleStatus": "${vehicleRequestDTO.vehicleStatus}" 
            }
        """.trimIndent()

        mockMvc.post("/api/v1/vehicles") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody
        }.andExpect {
            status { isCreated() }
            jsonPath("$.vin") { value("1HGBH41UU109186") }
        }

    }

    @Test
    fun `get AllMaintenainces should return a list of all the maintenances of the vehicle`() {
        mockMvc.get("/api/v1/vehicles/1/maintenances/") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content {
                jsonPath("$") {
                    isNotEmpty()
                    isArray()
                    value(hasSize<Int>(3))
                }
            }
        }
    }

    @Test
    @Transactional
    @Rollback
    fun `create new Maintenance for the the vehicle`() {

        val requestDTO = MaintenanceHistoryRequestDTO(
            maintenanceStatus = MaintenanceStatus.WAITING_FOR_PARTS,
            defect = "Oil leakage",
            service = "Oil change",
            maintenanceDescription = "Changed the oil"
        )

        val requestBody = """
            {
  "maintenanceStatus": "${requestDTO.maintenanceStatus}",
  "defect": "${requestDTO.defect}",
  "service": "${requestDTO.service}",
  "maintenanceDescription": "${requestDTO.maintenanceDescription}"
}
        """.trimIndent()

        mockMvc.post("/api/v1/vehicles/1/maintenances") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody
        }.andExpect {
            status { isCreated() }
            jsonPath("$.defect") { value(requestDTO.defect) }
        }
    }

    @Test
    fun `get all Notes for a vehicle`() {

        mockMvc.get("/api/v1/vehicles/1/notes/") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content {
                jsonPath("$") {
                    isNotEmpty()
                    isArray()
                    value(hasSize<Int>(2))
                }
                jsonPath("$[0].type") {
                    value("MAINTENANCE")
                }
            }
        }

    }

    @Test
    @Transactional
    @Rollback
    fun `create new Note for the the vehicle`() {

        val requestDTO = NoteRequestDTO(
            "License needs renewing",
            "Rental Manager",
            NoteType.GENERAL
        )

        val requestBody = """
            {
  "note": "${requestDTO.note}",
  "author": "${requestDTO.author}",
  "type": "${requestDTO.type}"
}
        """.trimIndent()

        mockMvc.post("/api/v1/vehicles/1/notes") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody
        }.andExpect {
            status { isCreated() }
            jsonPath("$.note") { value(requestDTO.note) }
        }
    }

    @Test
    @Transactional
    @Rollback
    fun `delete a Note for the the vehicle`() {
        mockMvc.delete("/api/v1/vehicles/1/notes/1") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNoContent() }
        }
    }

    @Test
    fun `get all Reservations for a vehicle`() {

        mockMvc.get("/api/v1/reservations") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content {
                jsonPath("$") {
                    isNotEmpty()
                    isArray()
                    value(hasSize<Int>(10))
                }
                jsonPath("$[0].totalPrice") {
                    value(466.04)
                }
            }
        }

    }

    @Test
    @Transactional
    @Rollback
    fun `create a new Car Model, create a new Vehicle and a Note, then delete the Note `() {

        val brand = "Fiat"
        val model = "600Turbo"
        val modelYear = 2006
        val segment = "Compact"
        val numberOfDoors = 4
        val seatingCapacity = 5
        val luggageCapacity = 400

        val mockRequestDTO = CarModelRequestDTO(
            brand,
            model,
            modelYear,
            segment,
            numberOfDoors,
            seatingCapacity,
            luggageCapacity,
            Category.ECONOMY,
            EngineType.HYBRID,
            TransmissionType.MANUAL,
            Drivetrain.FWD,
            motorDisplacement = 1.8,
            rentalPricePerDay = 50f
        )

        val requestBody = """
            {
    "brand": "${mockRequestDTO.brand}",
    "model": "${mockRequestDTO.brand}",
    "modelYear": ${mockRequestDTO.modelYear},
    "segment": "${mockRequestDTO.segment}",
    "numberOfDoors": ${mockRequestDTO.numberOfDoors},
     "seatingCapacity": ${mockRequestDTO.seatingCapacity},
    "luggageCapacity": ${mockRequestDTO.luggageCapacity},
     "category": "${mockRequestDTO.category}",
     "engineType": "${mockRequestDTO.engineType}",
     "transmissionType": "${mockRequestDTO.transmissionType}",
  "drivetrain": "${mockRequestDTO.drivetrain}",
  "motorDisplacement": ${mockRequestDTO.motorDisplacement},
  "features": [
    "Air Conditioning"
  ],
  "rentalPricePerDay": ${mockRequestDTO.rentalPricePerDay}
}
        """.trimIndent()

        val response = mockMvc.post("/api/v1/models") {
            accept = MediaType.APPLICATION_JSON
            content = requestBody
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            jsonPath("$.modelYear") { value(mockRequestDTO.modelYear) }
            jsonPath("$.rentalPricePerDay") { value(mockRequestDTO.rentalPricePerDay) }
        }.andReturn().response.contentAsString
        val carModelId: Int = JsonPath.parse(response).read("$.id")

        assertNotNull(carModelId)

        val vehicleRequestDTO = VehicleRequestDTO(
            3,
            "FK200LB2",
            "1HGBH41UU109186",
            15000,
            vehicleStatus = VehicleStatus.AVAILABLE
        )

        val requestBody1 = """
            {
                "carModelId": "$carModelId", 
                "licensePlate": "${vehicleRequestDTO.licensePlate}", 
                "vin": "${vehicleRequestDTO.vin}", 
                "kilometersTravelled": "${vehicleRequestDTO.kilometersTravelled}", 
                "vehicleStatus": "${vehicleRequestDTO.vehicleStatus}" 
            }
        """.trimIndent()

        val response1 = mockMvc.post("/api/v1/vehicles") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody1
        }.andExpect {
            status { isCreated() }
            jsonPath("$.vin") { value("1HGBH41UU109186") }
        }.andReturn().response.contentAsString
        val vehicleId: Int = JsonPath.parse(response1).read("$.id")

        assertNotNull(vehicleId)


        val requestDTO = NoteRequestDTO(
            "License needs renewing",
            "Rental Manager",
            NoteType.GENERAL
        )

        val requestBody2 = """
            {
  "note": "${requestDTO.note}",
  "author": "${requestDTO.author}",
  "type": "${requestDTO.type}"
}
        """.trimIndent()

        val response2 = mockMvc.post("/api/v1/vehicles/$vehicleId/notes") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody2
        }.andExpect {
            status { isCreated() }
            jsonPath("$.note") { value(requestDTO.note) }
        }.andReturn().response.contentAsString
        val noteId: Int = JsonPath.parse(response2).read("$.id")

        assertNotNull(noteId)

        mockMvc.delete("/api/v1/vehicles/$vehicleId/notes/$noteId") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNoContent() }
        }
    }

    @Test
    @Transactional
    @Rollback
    fun `Get a vehicle by ID, then add maintenance history and update it `() {

        val response = mockMvc.get("/api/v1/vehicles/1") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content {
                jsonPath("licensePlate") {
                    isNotEmpty()
                    value("FIAT500-001")
                }
            }
        }.andReturn().response.contentAsString
        val vehicleId: Int = JsonPath.parse(response).read("$.id")

        val requestDTO = MaintenanceHistoryRequestDTO(
            maintenanceStatus = MaintenanceStatus.WAITING_FOR_PARTS,
            defect = "Oil leakage",
            service = "Oil change",
            maintenanceDescription = "Changed the oil"
        )

        var requestBody = """
            {
  "maintenanceStatus": "${requestDTO.maintenanceStatus}",
  "defect": "${requestDTO.defect}",
  "service": "${requestDTO.service}",
  "maintenanceDescription": "${requestDTO.maintenanceDescription}"
}
        """.trimIndent()

        val response1 = mockMvc.post("/api/v1/vehicles/$vehicleId/maintenances") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody
        }.andExpect {
            status { isCreated() }
            jsonPath("$.defect") { value(requestDTO.defect) }
        }.andReturn().response.contentAsString
        val maintenanceId: Int = JsonPath.parse(response1).read("$.id")

        requestDTO.maintenanceStatus = MaintenanceStatus.WAITING_FOR_PARTS

        requestBody = """
            {
  "maintenanceStatus": "${requestDTO.maintenanceStatus}",
  "defect": "${requestDTO.defect}",
  "service": "${requestDTO.service}",
  "maintenanceDescription": "${requestDTO.maintenanceDescription}"
}
        """.trimIndent()

        mockMvc.put("/api/v1/vehicles/$vehicleId/maintenances/$maintenanceId") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody
        }.andExpect {
            status { isOk() }
            jsonPath("$.maintenanceStatus") { value(MaintenanceStatus.WAITING_FOR_PARTS.toString()) }
        }
    }

    @Test
    @Transactional
    @Rollback
    fun `Get a Reservation by id`() {

        val response = mockMvc.get("/api/v1/reservations/1") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content {
                jsonPath("totalPrice") {
                    isNotEmpty()
                    value(466.04)
                }
            }
        }.andReturn().response.contentAsString
        val reservationId: Int = JsonPath.parse(response).read("$.reservationId")

        assertNotNull(reservationId)
    }

    @Test
    fun `Get all reservations by user ID should return 404 if user not found`() {

        mockMvc.get("/api/v1/reservations/users/999") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }

    }

}