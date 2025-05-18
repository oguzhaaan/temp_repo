package g11.reservationservice.mappers

import g11.reservationservice.dtos.CarModelRequestDTO
import g11.reservationservice.dtos.CarModelResponseDTO
import g11.reservationservice.entities.*


// Convert CarModel Entity to CarModelResponseDTO
fun CarModel.toDto(): CarModelResponseDTO {
    return CarModelResponseDTO(
        id = this.getId(),
        brand = this.brandModel.brand.name,
        model = this.brandModel.model,
        modelYear = this.modelYear,
        segment = this.segment.name,
        numberOfDoors = this.numberOfDoors,
        seatingCapacity = this.seatingCapacity,
        luggageCapacity = this.luggageCapacity,
        category = this.category.toString(),
        engineType = this.engineType.toString(),
        transmissionType = this.transmissionType.toString(),
        drivetrain = this.drivetrain.toString(),
        motorDisplacement = this.motorDisplacement,
        features = this.features.map { feature -> feature.name }.toMutableSet(),
        rentalPricePerDay = this.rentalPricePerDay
    )
}

// Convert CarModelRequestDTO to CarModel Entity
fun CarModelRequestDTO.toEntity(
    features: List<Feature>,
    brandModel: BrandModel,
    segment: Segment,
): CarModel {

    return CarModel(
        brandModel = brandModel,
        modelYear = this.modelYear,
        segment = segment,
        numberOfDoors = this.numberOfDoors,
        seatingCapacity = this.seatingCapacity,
        luggageCapacity = this.luggageCapacity,
        category = this.category,
        engineType = this.engineType,
        transmissionType = this.transmissionType,
        drivetrain = this.drivetrain,
        motorDisplacement = this.motorDisplacement,
        features = features.toMutableSet(),
        rentalPricePerDay = this.rentalPricePerDay
    )
}

