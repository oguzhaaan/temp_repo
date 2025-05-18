package g11.reservationservice.mappers

import g11.reservationservice.dtos.TransferDTO
import g11.reservationservice.dtos.TransferResponseDTO
import g11.reservationservice.entities.Dropoff
import g11.reservationservice.entities.Pickup
import g11.reservationservice.entities.Reservation

fun Pickup.toResponseDto(): TransferResponseDTO {
    return TransferResponseDTO(
        reservationId = this.getId(),
        location = this.location,
        transferTime = this.timestamp,
        vehicleId = this.reservation.vehicle.getId(),
        handledByStaffId = this.handledByStaffId,
    )
}

fun Pickup.toDto(): TransferDTO {
    return TransferDTO(
        transferTime = this.timestamp,
        location = this.location,
        handledByStaffId = this.handledByStaffId,
    )
}

fun TransferDTO.toPickupEntity(reservation: Reservation): Pickup {
    return Pickup(
        reservation = reservation,
        timestamp = this.transferTime,
        location = this.location,
        handledByStaffId = this.handledByStaffId,
    )
}

fun Dropoff.toResponseDto(): TransferResponseDTO {
    return TransferResponseDTO(
        reservationId = this.getId(),
        location = this.location,
        transferTime = this.timestamp,
        vehicleId = this.reservation.vehicle.getId(),
        handledByStaffId = this.handledByStaffId,
    )
}

fun Dropoff.toDto(): TransferDTO {
    return TransferDTO(
        transferTime = this.timestamp,
        location = this.location,
        handledByStaffId = this.handledByStaffId,
    )
}

fun TransferDTO.toDropoffEntity(reservation: Reservation): Dropoff {
    return Dropoff(
        reservation = reservation,
        timestamp = this.transferTime,
        location = this.location,
        handledByStaffId = this.handledByStaffId,
    )
}