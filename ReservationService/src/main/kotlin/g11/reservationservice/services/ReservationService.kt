package g11.reservationservice.services

import g11.reservationservice.dtos.TransferDTO
import g11.reservationservice.dtos.TransferResponseDTO
import g11.reservationservice.dtos.ReservationResponseDTO
import g11.reservationservice.dtos.ReservationTimeIntervalDTO
import g11.reservationservice.entities.enumerations.ReservationStatus
import g11.reservationservice.entities.enumerations.TransferType

interface ReservationService {
    fun createReservation(
        userId: Long,
        carModelId: Long,
        reservationTimeInterval: ReservationTimeIntervalDTO
    ): ReservationResponseDTO

    fun updateReservationInfo(
        reservationId: Long,
        carModelId: Long? = null,
        reservationRequestDTO: ReservationTimeIntervalDTO
    ): ReservationResponseDTO

    fun updateReservationStatus(
        reservationId: Long,
        status: ReservationStatus
    ): ReservationResponseDTO

    fun getReservationById(reservationId: Long): ReservationResponseDTO

    fun getAllReservationsByUserId(userId: Long): List<ReservationResponseDTO>

    fun getAllReservations(): List<ReservationResponseDTO>

    fun deleteReservation(reservationId: Long)

    fun assignTransferToReservation(
        type: TransferType,
        reservationId: Long,
        transfer: TransferDTO
    ): TransferResponseDTO

    fun updateTransfer(
        type: TransferType,
        reservationId: Long,
        transfer: TransferDTO
    ): TransferResponseDTO

    fun updateReservation(reservationId: Long)

    fun removeTransferFromReservation(type: TransferType, reservationId: Long)

    fun initiatePaymentForReservation(reservationId: Long): String
}