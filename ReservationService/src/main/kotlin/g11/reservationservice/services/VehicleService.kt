package g11.reservationservice.services

import g11.reservationservice.dtos.*
import g11.reservationservice.entities.Vehicle

// methods to be implemented in VehicleServiceImpl
interface VehicleService {
    fun getVehicleById(id: Long): VehicleResponseDTO
    fun getVehicleInstanceById(id: Long): Vehicle
    fun addNewVehicle(vehicleDTO: VehicleRequestDTO): VehicleResponseDTO
    fun updateVehicle(updateDTO: VehicleUpdateDTO, id: Long): VehicleResponseDTO
    fun getAllVehicles(page: Int, size: Int, sortBy: String, direction: String, filter: VehicleFilterDTO):
            List<VehicleResponseDTO>

    fun deleteVehicleById(id: Long)
    fun findAvailableVehicleForReservation(
        carModelId: Long,
        timeInterval: ReservationTimeIntervalDTO
    ): Vehicle
    fun deleteAllVehicles()
}