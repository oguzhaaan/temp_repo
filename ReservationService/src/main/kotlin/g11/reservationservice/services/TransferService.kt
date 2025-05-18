package g11.reservationservice.services

import g11.reservationservice.dtos.TransferDTO
import g11.reservationservice.dtos.TransferResponseDTO
import g11.reservationservice.entities.enumerations.TransferType

interface TransferService {
    fun createTransfer(type: TransferType, reservationId: Long, transfer: TransferDTO): TransferResponseDTO
    fun updateTransfer(type: TransferType, reservationId: Long, transfer: TransferDTO): TransferResponseDTO
    fun deleteTransfer(type: TransferType, transferId: Long)
    fun getTransferById(type: TransferType, reservationId: Long): TransferResponseDTO
    fun getAllTransfers(type: TransferType): List<TransferResponseDTO>
}