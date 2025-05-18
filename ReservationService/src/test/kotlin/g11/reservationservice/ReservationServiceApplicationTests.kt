package g11.reservationservice

import g11.reservationservice.dtos.CarModelFilterDTO
import g11.reservationservice.dtos.CarModelRequestDTO
import g11.reservationservice.dtos.CarModelUpdateDTO
import g11.reservationservice.entities.Brand
import g11.reservationservice.entities.BrandModel
import g11.reservationservice.entities.enumerations.Category
import g11.reservationservice.entities.enumerations.Drivetrain
import g11.reservationservice.entities.enumerations.EngineType
import g11.reservationservice.entities.enumerations.TransmissionType
import g11.reservationservice.exceptions.CarModelNotFoundException
import g11.reservationservice.services.BrandModelService
import g11.reservationservice.services.BrandService
import g11.reservationservice.services.CarModelService
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class ReservationServiceApplicationTests: IntegrationTest() {
	@Autowired
	private lateinit var brandService: BrandService

	@Test
	@Transactional
	@Rollback
	fun findOrCreateBrands() {
		val brand = "New Brand Test"
		brandService.findOrCreateBrand(brand)

		val brandList = brandService.getAllBrands()

		assert(brandList.isNotEmpty())
		assert(brandList.contains(brand))
	}

	@Autowired
	private lateinit var brandModelService: BrandModelService

	@Test
	@Transactional
	@Rollback
	fun brandModelFindOrCreate() {
		val brand = "Lancia"
		val model = "Delta Integrale"

		brandModelService.findOrCreateBrandModel(brand, model)

		val brandModelList = brandModelService.getAllBrandModels()

		assert(brandModelList.isNotEmpty())
		assert(brandModelList.contains(brand))
		brandModelList[brand]?.let { assert(it.contains(model)) }
	}

	@Test
	@Transactional
	@Rollback
	fun getBrandModelsByBrand() {
		val brand = "Kawasaki"
		val model1 = "Eliminator 500."
		val model2 = "KX250X."
		val model3 = "Ninja 1000 SX."

		brandService.addBrand(brand)
		brandModelService.addBrandModel(BrandModel(Brand(brand), model1))
		brandModelService.addBrandModel(BrandModel(Brand(brand), model2))
		brandModelService.addBrandModel(BrandModel(Brand(brand), model3))

		val brandModelList = brandModelService.getModelsByBrand(brand)

		assert(brandModelList.size == 3)
		assert(brandModelList.contains(model1))
		assert(brandModelList.contains(model2))
		assert(brandModelList.contains(model3))
	}

	@Autowired
	private lateinit var carModelService: CarModelService

	@Test
	@Transactional
	@Rollback
	fun createCarModel(){

		val brand = "Fiat"
		val model = "500XL"
		val modelYear = 2001
		val segment = "Compact"
		val numberOfDoors = 4
		val seatingCapacity = 5
		val luggageCapacity = 400

		val num = carModelService.getAllCarModels(
			0,
			25,
			"id",
			"asc",
			CarModelFilterDTO()
		).size

		val mockRequestDTO = CarModelRequestDTO(
			brand,
			model,
			modelYear,
			segment,
			numberOfDoors,
			seatingCapacity,
			luggageCapacity,
			Category.ECONOMY,
			EngineType.HYBRID,
			TransmissionType.MANUAL,
			Drivetrain.FWD,
			motorDisplacement = 1.8,
			rentalPricePerDay = 50f
		)

		val responseDTO = carModelService.createCarModel(mockRequestDTO)

		val carModelList = carModelService.getAllCarModels(
			0,
			25,
			"id",
			"asc",
			CarModelFilterDTO()
		)


		// Size is checked before and after creating
		assert(carModelList.size == num+1)

		val found = carModelList.stream().anyMatch {
			it.brand == brand &&
					it.model == model &&
					it.modelYear == modelYear &&
					it.segment == segment &&
					it.numberOfDoors == numberOfDoors &&
					it.seatingCapacity == seatingCapacity &&
					it.luggageCapacity == luggageCapacity &&
					it.category == Category.ECONOMY.toString() &&
					it.engineType == EngineType.HYBRID.toString() &&
					it.transmissionType == TransmissionType.MANUAL.toString() &&
					it.drivetrain == Drivetrain.FWD.toString() &&
					it.motorDisplacement == 1.8 &&
					it.rentalPricePerDay == 50f
		}

		assert(found)


	}

	@Test
	@Transactional
	@Rollback
	fun getCarModelById(){

		val carModel = carModelService.getCarModelById(1)

		assert(carModel.model == "500")
		assert(carModel.brand == "Fiat")
	}

	@Test
	@Transactional
	@Rollback
	fun updateCarModel(){
		val carModelUpdateDTO = CarModelUpdateDTO(
			brand = "Fiat",
			 "500",
			2002,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null
		)

		val carModel = carModelService.updateCarModel(1, carModelUpdateDTO)

		assert(carModel.model == "500")
		assert(carModel.brand == "Fiat")
		assert(carModel.modelYear == 2002)
	}

	@Test
	@Transactional
	@Rollback
	fun deleteCarModel(){
		carModelService.deleteCarModel(1)

		assertThrows<CarModelNotFoundException> {
			carModelService.getCarModelById(1)
		}
	}
}

