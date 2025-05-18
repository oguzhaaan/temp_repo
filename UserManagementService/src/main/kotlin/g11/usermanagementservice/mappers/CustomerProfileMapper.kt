package g11.usermanagementservice.mappers

import g11.usermanagementservice.dtos.CustomerProfileRequestDTO
import g11.usermanagementservice.dtos.CustomerProfileResponseDTO
import g11.usermanagementservice.entities.CustomerProfile
import g11.usermanagementservice.entities.User

fun CustomerProfile.toDto(): CustomerProfileResponseDTO {
    return CustomerProfileResponseDTO(
        reliabilityScore = this.reliabilityScore,
        drivingLicenseNumber = this.drivingLicenseNumber,
        drivingLicenseExpiry = this.drivingLicenseExpiry,
        preferences = this.preferences,
    )
}

fun CustomerProfileRequestDTO.toEntity(user: User, reliabilityScore: Float): CustomerProfile {
    return CustomerProfile(
        user = user,
        reliabilityScore = reliabilityScore,
        drivingLicenseNumber = this.drivingLicenseNumber,
        drivingLicenseExpiry = this.drivingLicenseExpiry,
        preferences = this.preferences
    )
}