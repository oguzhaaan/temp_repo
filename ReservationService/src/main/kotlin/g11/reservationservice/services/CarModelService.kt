package g11.reservationservice.services

import g11.reservationservice.dtos.*

interface CarModelService {
    fun getAllCarModels(
        page: Int,
        size: Int,
        sortBy: String,
        direction: String,
        carModelFilterDTO: CarModelFilterDTO
    ): List<CarModelResponseDTO>

    fun getCarModelById(id: Long): CarModelResponseDTO
    fun createCarModel(carModel: CarModelRequestDTO): CarModelResponseDTO
    fun updateCarModel(id: Long, updateDTO: CarModelUpdateDTO): CarModelResponseDTO
    fun deleteCarModel(id: Long)
    fun findCarModelsWithAvailableVehicles(timeInterval: ReservationTimeIntervalDTO)
            : List<CarModelResponseDTO>
}