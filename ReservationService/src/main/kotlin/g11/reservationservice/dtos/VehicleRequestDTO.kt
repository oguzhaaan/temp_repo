package g11.reservationservice.dtos

import g11.reservationservice.entities.enumerations.VehicleStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class VehicleRequestDTO(

    @field:NotNull(message = "Car model must be defined")
    var carModelId : Long,

    @field:NotBlank(message = "License plate must be defined")
    val licensePlate : String,

    @field:NotBlank(message = "Vin must be defined")
    @field:Size(max = 17, message = "Vin must not be longer than 17 characters")
    val vin : String,

    @field:NotNull
    val kilometersTravelled : Int,

    @field:NotNull
    val vehicleStatus: VehicleStatus,

)