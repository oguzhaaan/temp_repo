package g11.usermanagementservice.services

import g11.usermanagementservice.dtos.CustomerProfileRequestDTO
import g11.usermanagementservice.dtos.CustomerProfileResponseDTO
import g11.usermanagementservice.entities.CustomerProfile
import g11.usermanagementservice.entities.User

interface CustomerProfileService {
    fun createProfileEntity(
        dto: CustomerProfileRequestDTO, user: User
    ): CustomerProfile

    fun updateExistingProfile(
        profile: CustomerProfile, dto: CustomerProfileRequestDTO
    )

    fun deleteProfile(profile: CustomerProfile)

}