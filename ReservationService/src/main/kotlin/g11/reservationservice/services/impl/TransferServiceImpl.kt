package g11.reservationservice.services.impl

import g11.reservationservice.dtos.TransferDTO
import g11.reservationservice.dtos.TransferResponseDTO
import g11.reservationservice.entities.enumerations.TransferType
import g11.reservationservice.exceptions.TransferNotFoundException
import g11.reservationservice.repositories.DropoffRepository
import g11.reservationservice.repositories.PickupRepository
import g11.reservationservice.services.ReservationService
import g11.reservationservice.services.TransferService
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TransferServiceImpl(
    private val pickupRepository: PickupRepository,
    private val dropoffRepository: DropoffRepository,
    private val reservationService: ReservationService
) : TransferService {

    private val logger = LoggerFactory.getLogger(TransferServiceImpl::class.java)

    @Transactional
    override fun createTransfer(type: TransferType, reservationId: Long, transfer: TransferDTO): TransferResponseDTO {
        return reservationService.assignTransferToReservation(type, reservationId, transfer)
    }

    @Transactional
    override fun updateTransfer(type: TransferType, reservationId: Long, transfer: TransferDTO): TransferResponseDTO {
        return reservationService.updateTransfer(type, reservationId, transfer)
    }

    @Transactional
    override fun deleteTransfer(type: TransferType, transferId: Long) {
        val reservationId = when (type) {
            TransferType.PICKUP -> pickupRepository.findByIdOrNull(transferId)
                ?.reservation?.getId()
                ?: throw TransferNotFoundException("Pickup with ID $transferId not found")

            TransferType.DROPOFF -> dropoffRepository.findByIdOrNull(transferId)
                ?.reservation?.getId()
                ?: throw TransferNotFoundException("Dropoff with ID $transferId not found")
        }

        reservationService.removeTransferFromReservation(type, reservationId)

        // if type is pickup, delete from pickup repository
        // if type is dropoff, delete from dropoff repository
        when (type) {
            TransferType.PICKUP -> pickupRepository.deleteById(transferId)
            TransferType.DROPOFF -> dropoffRepository.deleteById(transferId)
        }

        logger.info("${type.name} with ID $transferId deleted from reservation ID $reservationId")
    }

    @Transactional(readOnly = true)
    override fun getTransferById(type: TransferType, reservationId: Long): TransferResponseDTO {
        val reservation = reservationService.getReservationById(reservationId)
        val transfer = when (type) {
            TransferType.PICKUP -> reservation.pickup
            TransferType.DROPOFF -> reservation.dropoff
        } ?: throw TransferNotFoundException("${type.name} not available for reservation ID $reservationId")

        return TransferResponseDTO(
            reservationId = reservation.reservationId,
            vehicleId = reservation.vehicle.id,
            location = transfer.location,
            transferTime = transfer.transferTime,
            handledByStaffId = transfer.handledByStaffId
        )
    }

    @Transactional(readOnly = true)
    override fun getAllTransfers(type: TransferType): List<TransferResponseDTO> {
        return reservationService.getAllReservations()
            .mapNotNull { reservation ->
                val transfer = when (type) {
                    TransferType.PICKUP -> reservation.pickup
                    TransferType.DROPOFF -> reservation.dropoff
                }
                transfer?.let {
                    TransferResponseDTO(
                        reservationId = reservation.reservationId,
                        vehicleId = reservation.vehicle.id,
                        location = it.location,
                        transferTime = it.transferTime,
                        handledByStaffId = it.handledByStaffId
                    )
                }
            }
    }

}
