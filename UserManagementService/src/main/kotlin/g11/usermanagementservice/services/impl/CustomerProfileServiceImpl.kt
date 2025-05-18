package g11.usermanagementservice.services.impl

import g11.usermanagementservice.dtos.CustomerProfileRequestDTO
import g11.usermanagementservice.dtos.CustomerProfileResponseDTO
import g11.usermanagementservice.entities.CustomerProfile
import g11.usermanagementservice.entities.User
import g11.usermanagementservice.entities.enumerations.Role
import g11.usermanagementservice.exceptions.InvalidUserException
import g11.usermanagementservice.mappers.toDto
import g11.usermanagementservice.repositories.CustomerProfileRepository
import g11.usermanagementservice.services.CustomerProfileService
import g11.usermanagementservice.mappers.toEntity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CustomerProfileServiceImpl (
    private val customerProfileRepository: CustomerProfileRepository,
) : CustomerProfileService {

    private val logger = LoggerFactory.getLogger(UserServiceImpl::class.java)

    override fun createProfileEntity(dto: CustomerProfileRequestDTO, user: User): CustomerProfile {
        val profile = dto.toEntity(user, 0f)
        validateCustomerProfile(profile)
        return profile // don't save it here! user will save it
    }

    override fun updateExistingProfile(profile: CustomerProfile, dto: CustomerProfileRequestDTO) {
        profile.drivingLicenseExpiry = dto.drivingLicenseExpiry
        profile.drivingLicenseNumber = dto.drivingLicenseNumber
        profile.preferences.clear()
        profile.preferences.addAll(dto.preferences)

        validateCustomerProfile(profile)
        // no need to save — it will be flushed with User
    }

    override fun deleteProfile(profile: CustomerProfile) {
        customerProfileRepository.delete(profile)
    }

    private fun validateCustomerProfile(profile: CustomerProfile) {
        val licenseNumberPattern = Regex("^[A-Z0-9]{8,20}$")

        if (profile.drivingLicenseNumber.isBlank()) {
            throw InvalidUserException("Driving license number is required.")
        }

        if (!licenseNumberPattern.matches(profile.drivingLicenseNumber)) {
            throw InvalidUserException("Driving license number must be 8 to 20 uppercase letters or digits.")
        }

        if (profile.drivingLicenseExpiry.isBefore(LocalDate.now())) {
            throw InvalidUserException("Driving license must not be expired.")
        }
    }
}

