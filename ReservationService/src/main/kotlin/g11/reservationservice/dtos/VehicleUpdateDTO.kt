package g11.reservationservice.dtos

import g11.reservationservice.entities.enumerations.VehicleStatus

data class VehicleUpdateDTO(
    val carModelId: Long?,
    val licensePlate: String?,
    val vin: String?,
    val kilometersTravelled: Int?,
    val vehicleStatus: VehicleStatus?
)

