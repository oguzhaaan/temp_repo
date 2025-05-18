package g11.reservationservice.dtos

import g11.reservationservice.entities.enumerations.VehicleStatus

data class VehicleFilterDTO(
    val licensePlate: String? = null,
    val vin: String? = null,
    val minKilometers: Int? = null,
    val maxKilometers: Int? = null,
    val statuses: List<VehicleStatus>? = null,
    val modelIds: List<Long>? = null
)

