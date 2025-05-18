package g11.reservationservice.mappers

import g11.reservationservice.dtos.ReservationResponseDTO
import g11.reservationservice.dtos.VehicleResponseDTO
import g11.reservationservice.entities.Reservation

fun Reservation.toDTO(vehicleDTO: VehicleResponseDTO) : ReservationResponseDTO = ReservationResponseDTO(
    reservationId = this.getId(),
    userId = this.userId,
    vehicle = vehicleDTO,
    startDate = this.startDate,
    endDate = this.endDate,
    reservationStatus = this.reservationStatus,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    totalPrice = this.totalPrice,
    cancellationDate = this.cancellationDate,
    pickup = this.pickup?.toDto(),
    dropoff = this.dropoff?.toDto(),
)
