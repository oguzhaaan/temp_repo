package g11.reservationservice.services.impl

import g11.reservationservice.dtos.*
import g11.reservationservice.entities.Dropoff
import g11.reservationservice.entities.Pickup
import g11.reservationservice.entities.Reservation
import g11.reservationservice.entities.enumerations.MaintenanceStatus
import g11.reservationservice.entities.enumerations.ReservationStatus
import g11.reservationservice.entities.enumerations.TransferType
import g11.reservationservice.entities.enumerations.VehicleStatus
import g11.reservationservice.exceptions.*
import g11.reservationservice.mappers.*
import g11.reservationservice.repositories.ReservationRepository
import g11.reservationservice.services.*
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class ReservationServiceImpl(
    private val reservationRepository: ReservationRepository,
    private val vehicleService: VehicleService,
    private val carModelService: CarModelService,
    private val userClient: UserClient,
    private val maintenanceHistoryService: MaintenanceHistoryService,
    private val paymentClient: PaymentServiceClient,
) : ReservationService {

    private val logger = LoggerFactory.getLogger(CarModelServiceImpl::class.java)

    @Transactional
    override fun createReservation(
        userId: Long,
        carModelId: Long,
        reservationTimeInterval: ReservationTimeIntervalDTO
    ): ReservationResponseDTO {

        // check if user exists
        val user = userClient.getUserById(userId)

        // check if user has role CUSTOMER
        if (user.role != "CUSTOMER") {
            throw UnauthorizedUserException("Only customers can make reservations.")
        }

        // check if user has completed his profile
        val customerProfile = user.customerProfile
            ?: throw MissingCustomerProfileException()

        // check the validity of customer profile
        // driving license expiry date must be valid at given time interval
        if (customerProfile.drivingLicenseExpiry.isBefore(reservationTimeInterval.endDate)) {
            throw InvalidDrivingLicenseException()
        }

        // check if car model exists
        val carModel = carModelService.getCarModelById(carModelId)

        // find an available vehicle for given car model to assign it to reservation
        val vehicle = vehicleService.findAvailableVehicleForReservation(carModelId, reservationTimeInterval)

        // calculate total rent price
        val days = ChronoUnit.DAYS.between(reservationTimeInterval.startDate, reservationTimeInterval.endDate)
        val totalPrice = carModel.rentalPricePerDay * days

        // create reservation object
        val reservation = Reservation(
            userId = userId,
            vehicle = vehicle,
            startDate = reservationTimeInterval.startDate,
            endDate = reservationTimeInterval.endDate,
            createdAt = LocalDateTime.now(),
            totalPrice = totalPrice,
            reservationStatus = ReservationStatus.WAITING_FOR_PAYMENT,
        )

        // save reservation
        val saved = reservationRepository.save(reservation)

        // log
        logger.info(
            "Reservation with ID ${saved.getId()} created successfully. Vehicle ID: ${vehicle.getId()} | User ID: $userId | Start Date: ${reservation.startDate} | End Date: ${reservation.endDate}"
        )

        return saved.toDTO(vehicle.toResponseDTO())
    }


    @Transactional
    override fun updateReservationInfo(
        reservationId: Long,
        carModelId: Long?,
        reservationRequestDTO: ReservationTimeIntervalDTO
    ): ReservationResponseDTO {
        val reservation = reservationRepository.findByIdOrNull(reservationId)
            ?: throw ReservationNotFoundException(reservationId)

        val user = userClient.getUserById(reservation.userId)

        if (user.role != "CUSTOMER") {
            throw UnauthorizedUserException("Only customers can make reservations.")
        }

        val customerProfile = user.customerProfile
            ?: throw MissingCustomerProfileException()

        if (customerProfile.drivingLicenseExpiry.isBefore(reservationRequestDTO.endDate)) {
            throw InvalidDrivingLicenseException()
        }

        val vehicle = if (carModelId != null) {
            // fetch model and a new vehicle
            carModelService.getCarModelById(carModelId)
            vehicleService.findAvailableVehicleForReservation(carModelId, reservationRequestDTO)
        } else {
            // Try to keep current vehicle
            val currentVehicle = reservation.vehicle
            val isAvailable = isVehicleAvailableForReservation(
                currentVehicle.getId(),
                reservationRequestDTO,
                excludeReservationId = reservation.getId()
            )

            if (isAvailable) {
                currentVehicle
            } else {
                // Try another vehicle from the same model
                vehicleService.findAvailableVehicleForReservation(
                    currentVehicle.carModel.getId(),
                    reservationRequestDTO
                )
            }
        }

        reservation.vehicle = vehicle
        reservation.startDate = reservationRequestDTO.startDate
        reservation.endDate = reservationRequestDTO.endDate

        val days = ChronoUnit.DAYS.between(reservation.startDate, reservation.endDate)
        reservation.totalPrice = vehicle.carModel.rentalPricePerDay * days

        val updated = reservationRepository.save(reservation)

        logger.info(
            "Reservation ${updated.getId()} updated. Vehicle: ${vehicle.getId()}, User: ${updated.userId}, Dates: ${updated.startDate} to ${updated.endDate}"
        )

        return updated.toDTO(vehicle.toResponseDTO())
    }

    @Transactional
    override fun updateReservationStatus(reservationId: Long, status: ReservationStatus): ReservationResponseDTO {
        val reservation = reservationRepository.findByIdOrNull(reservationId)
            ?: throw ReservationNotFoundException(reservationId)

        // only reservation with status CONFIRMED can be cancelled
        if (status == ReservationStatus.CANCELLED && reservation.reservationStatus != ReservationStatus.WAITING_FOR_PAYMENT) {
            throw InvalidReservationStatusException("Reservation can only be cancelled if it is in WAITING_FOR_PAYMENT status.")
        }
        // on cancellation, set the cancellation date
        if (status == ReservationStatus.CANCELLED) {
            reservation.cancellationDate = LocalDateTime.now()
        }

        // on ONGOING, the vehicle status must be set to RENTED
        if (status == ReservationStatus.ONGOING) {
            reservation.vehicle.vehicleStatus = VehicleStatus.RENTED
        }

        // on COMPLETED, the vehicle status must be set to AVAILABLE
        if (status == ReservationStatus.COMPLETED) {
            reservation.vehicle.vehicleStatus = VehicleStatus.AVAILABLE
        }

        reservation.reservationStatus = status
        reservation.updatedAt = LocalDateTime.now()

        val updatedReservation = reservationRepository.save(reservation)

        logger.info("Reservation with ID ${updatedReservation.getId()} status updated to $status.")
        return updatedReservation.toDTO(reservation.vehicle.toResponseDTO())
    }

    @Transactional(readOnly = true)
    override fun getReservationById(reservationId: Long): ReservationResponseDTO {
        // check if reservation exists
        val reservation = reservationRepository.findByIdOrNull(reservationId)
            ?: throw ReservationNotFoundException(reservationId)

        // return reservation as response DTO
        return reservation.toDTO(reservation.vehicle.toResponseDTO())
    }

    @Transactional(readOnly = true)
    override fun getAllReservationsByUserId(userId: Long): List<ReservationResponseDTO> {
        // check if user has role CUSTOMER, if not throw exception
        val user = userClient.getUserById(userId)
        if (user.role != "CUSTOMER") {
            throw UnauthorizedUserException("Only customers can view their reservations.")
        }

        // get all reservations by user ID
        val reservations = reservationRepository.findAllByUserId(userId)

        // return list of reservations
        return reservations.map { it.toDTO(it.vehicle.toResponseDTO()) }
    }

    @Transactional(readOnly = true)
    override fun getAllReservations(): List<ReservationResponseDTO> {
        // get all reservations
        val reservations = reservationRepository.findAll()

        // return list of reservations
        return reservations.map { it.toDTO(it.vehicle.toResponseDTO()) }
    }

    @Transactional
    override fun deleteReservation(reservationId: Long) {
        val reservation = reservationRepository.findByIdOrNull(reservationId)
            ?: throw ReservationNotFoundException(reservationId)
        // only reservation with status COMPLETED or CANCELED can be deleted
        if (reservation.reservationStatus != ReservationStatus.COMPLETED && reservation.reservationStatus != ReservationStatus.CANCELLED) {
            throw InvalidReservationStatusException("Reservation can only be deleted if it is in COMPLETED or CANCELLED status.")
        }
        reservationRepository.delete(reservation)
        logger.info("Reservation with ID $reservationId deleted.")
    }

    @Transactional
    fun isVehicleAvailableForReservation(
        vehicleId: Long,
        timeInterval: ReservationTimeIntervalDTO,
        excludeReservationId: Long? = null
    ): Boolean {
        vehicleService.getVehicleById(vehicleId)

        // 1. Check maintenance conflicts
        val hasUpcomingMaintenance = maintenanceHistoryService.ifMaintenanceExists(
            vehicleId,
            MaintenanceStatus.UPCOMING,
            timeInterval
        )

        if (hasUpcomingMaintenance) return false

        // 2. Check conflicting reservations (excluding a specific reservation if updating)
        val overlappingReservations = if (excludeReservationId != null) {
            reservationRepository.existsByVehicleIdAndReservationStatusNotAndIdNotAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                vehicleId,
                ReservationStatus.CANCELLED,
                excludeReservationId,
                timeInterval.endDate,
                timeInterval.startDate
            )
        } else {
            reservationRepository.existsByVehicleIdAndReservationStatusNotAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                vehicleId,
                ReservationStatus.CANCELLED,
                timeInterval.endDate,
                timeInterval.startDate
            )
        }

        return !overlappingReservations
    }


    @Transactional
    override fun assignTransferToReservation(
        type: TransferType,
        reservationId: Long,
        transfer: TransferDTO
    ): TransferResponseDTO {
        val reservation = reservationRepository.findByIdOrNull(reservationId)
            ?: throw ReservationNotFoundException(reservationId)

        if ((type == TransferType.PICKUP && reservation.pickup != null) ||
            (type == TransferType.DROPOFF && reservation.dropoff != null)) {
            throw IllegalStateException("${type.name.capitalize()} already exists for reservation ID: $reservationId. Use update instead.")
        }

        val transferEntity = when (type) {
            TransferType.PICKUP -> applyPickupUpdate(reservation, transfer)
            TransferType.DROPOFF -> applyDropoffUpdate(reservation, transfer)
        }

        if (type == TransferType.PICKUP) reservation.pickup = transferEntity as Pickup
        if (type == TransferType.DROPOFF) reservation.dropoff = transferEntity as Dropoff

        reservationRepository.save(reservation)
        logger.info("${type.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} assigned to reservation ID: $reservationId")

        return when (transferEntity) {
            is Pickup -> transferEntity.toResponseDto()
            is Dropoff -> transferEntity.toResponseDto()
            else -> throw IllegalStateException("Unknown transfer entity type")
        }
    }

    @Transactional
    override fun updateTransfer(
        type: TransferType,
        reservationId: Long,
        transfer: TransferDTO
    ): TransferResponseDTO {
        val reservation = reservationRepository.findByIdOrNull(reservationId)
            ?: throw ReservationNotFoundException(reservationId)

        val transferEntity = when (type) {
            TransferType.PICKUP -> applyPickupUpdate(reservation, transfer)
            TransferType.DROPOFF -> applyDropoffUpdate(reservation, transfer)
        }

        if (type == TransferType.PICKUP) reservation.pickup = transferEntity as Pickup
        if (type == TransferType.DROPOFF) reservation.dropoff = transferEntity as Dropoff

        reservationRepository.save(reservation)
        logger.info("${type.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} updated for reservation ID: $reservationId")

        return when (transferEntity) {
            is Pickup -> transferEntity.toResponseDto()
            is Dropoff -> transferEntity.toResponseDto()
            else -> throw IllegalStateException("Unknown transfer entity type")
        }
    }

    override fun updateReservation(reservationId: Long) {
        // get reservation by ID
        val reservation = reservationRepository.findByIdOrNull(reservationId)
            ?: throw ReservationNotFoundException(reservationId)
        // update updatedAt field to current time
        reservation.updatedAt = LocalDateTime.now()
        // save reservation
        reservationRepository.save(reservation)
    }

    @Transactional
    override fun removeTransferFromReservation(type: TransferType, reservationId: Long) {
        val reservation = reservationRepository.findByIdOrNull(reservationId)
            ?: throw ReservationNotFoundException(reservationId)

        when (type) {
            TransferType.PICKUP -> {
                reservation.pickup = null
                reservation.updatedAt = LocalDateTime.now()
                logger.info("Pickup unlinked from reservation ID $reservationId")
            }
            TransferType.DROPOFF -> {
                reservation.dropoff = null
                reservation.updatedAt = LocalDateTime.now()
                logger.info("Dropoff unlinked from reservation ID $reservationId")
            }
        }

        reservationRepository.save(reservation)
    }


    private fun applyPickupUpdate(reservation: Reservation, pickup: TransferDTO): Pickup {
        validatePickupInput(reservation, pickup)

        return reservation.pickup?.apply {
            timestamp = pickup.transferTime
            location = pickup.location
            handledByStaffId = pickup.handledByStaffId
        } ?: pickup.toPickupEntity(reservation)
    }

    private fun applyDropoffUpdate(reservation: Reservation, dropoff: TransferDTO): Dropoff {
        validateDropoffInput(reservation, dropoff)

        return reservation.dropoff?.apply {
            timestamp = dropoff.transferTime
            location = dropoff.location
            handledByStaffId = dropoff.handledByStaffId
        } ?: dropoff.toDropoffEntity(reservation)
    }

    private fun validatePickupInput(reservation: Reservation, transfer: TransferDTO) {
        // Pickup-specific validation (timestamp and reservation status check)
        if (reservation.reservationStatus != ReservationStatus.CONFIRMED) {
            throw InvalidReservationStatusException("Pickup can only be assigned to a reservation in CONFIRMED status.")
        }

        if (transfer.transferTime.toLocalDate() != reservation.startDate) {
            throw TransferDateMismatchException("Pickup date must be same as the reservation start date.")
        }

        // Additional validation for staff
        transfer.handledByStaffId?.let { staffId ->
            val staff = userClient.getUserById(staffId)
            if (staff.role != "STAFF") {
                throw UnauthorizedUserException("User with ID $staffId is not a staff member.")
            }
        }
    }

    private fun validateDropoffInput(reservation: Reservation, transfer: TransferDTO) {
        // Dropoff-specific validation (timestamp and reservation status check)
        if (reservation.reservationStatus != ReservationStatus.CONFIRMED && reservation.reservationStatus != ReservationStatus.ONGOING) {
            throw InvalidReservationStatusException("Dropoff can only be assigned to a reservation in CONFIRMED or ONGOING status.")
        }

        if (transfer.transferTime.toLocalDate() != reservation.endDate) {
            throw TransferDateMismatchException("Dropoff date must be same as the reservation end date.")
        }

        // Additional validation for staff
        transfer.handledByStaffId?.let { staffId ->
            val staff = userClient.getUserById(staffId)
            if (staff.role != "STAFF") {
                throw UnauthorizedUserException("User with ID $staffId is not a staff member.")
            }
        }
    }

    override fun initiatePaymentForReservation(reservationId: Long): String {
        val reservation = reservationRepository.findById(reservationId)
            .orElseThrow { IllegalArgumentException("Reservation not found") }


        val paymentRequest = PaymentInitiationRequest(
            reservationId = reservation.getId(),
            userId = reservation.userId,
            amount = reservation.totalPrice,
            currency = "EUR"
        )

        val response = paymentClient.initiatePayment(paymentRequest)

        return response.approvalUrl
    }
}