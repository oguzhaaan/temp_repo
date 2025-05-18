package g11.reservationservice.mappers

import g11.reservationservice.dtos.VehicleRequestDTO
import g11.reservationservice.dtos.VehicleResponseDTO
import g11.reservationservice.entities.CarModel
import g11.reservationservice.entities.Vehicle

fun Vehicle.toResponseDTO(): VehicleResponseDTO = VehicleResponseDTO(
    this.getId(), this.carModel.toDto(), this.licensePlate,
    this.vin, this.kilometersTravelled, this.vehicleStatus,
)

fun VehicleRequestDTO.toEntity(
    carModel: CarModel
): Vehicle = Vehicle(
    carModel,
    this.licensePlate,
    this.vin,
    this.kilometersTravelled,
    this.vehicleStatus
)
