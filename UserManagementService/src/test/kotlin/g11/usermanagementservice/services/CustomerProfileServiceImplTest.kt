package g11.usermanagementservice.services

import g11.usermanagementservice.dtos.CustomerProfileRequestDTO
import g11.usermanagementservice.entities.CustomerProfile
import g11.usermanagementservice.entities.User
import g11.usermanagementservice.entities.enumerations.Role
import g11.usermanagementservice.entities.enumerations.Preference
import g11.usermanagementservice.exceptions.InvalidUserException
import g11.usermanagementservice.repositories.CustomerProfileRepository
import g11.usermanagementservice.services.impl.CustomerProfileServiceImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class CustomerProfileServiceImplTest {

    @Mock
    lateinit var customerProfileRepository: CustomerProfileRepository

    @InjectMocks
    lateinit var customerProfileService: CustomerProfileServiceImpl

    private fun createMockUser(): User {
        return User(
            username = "testuser",
            firstName = "Test",
            lastName = "User",
            email = "testuser@example.com",
            phoneNumber = "1234567890",
            role = Role.CUSTOMER,
            birthDate = LocalDate.of(1995, 1, 1)
        )
    }

    private fun createValidProfileRequest(): CustomerProfileRequestDTO {
        return CustomerProfileRequestDTO(
            drivingLicenseNumber = "A1234567",
            drivingLicenseExpiry = LocalDate.now().plusYears(2),
            preferences = mutableSetOf(Preference.EMAIL_NOTIFICATIONS, Preference.SMS_NOTIFICATIONS)
        )
    }

    @Test
    fun `createProfileEntity should succeed with valid data`() {
        val user = createMockUser()
        val request = createValidProfileRequest()

        val profile = customerProfileService.createProfileEntity(request, user)

        assertEquals(request.drivingLicenseNumber, profile.drivingLicenseNumber)
        assertEquals(request.drivingLicenseExpiry, profile.drivingLicenseExpiry)
        assertEquals(user, profile.user)
        assertEquals(request.preferences, profile.preferences)
    }

    @Test
    fun `createProfileEntity should throw InvalidUserException for invalid license number`() {
        val user = createMockUser()
        val request = createValidProfileRequest().copy(drivingLicenseNumber = "bad")

        val exception = assertThrows<InvalidUserException> {
            customerProfileService.createProfileEntity(request, user)
        }

        assertTrue(exception.message!!.contains("Driving license number must be 8 to 20 uppercase letters or digits"))
    }

    @Test
    fun `updateExistingProfile should succeed with valid update`() {
        val user = createMockUser()
        val existingProfile = CustomerProfile(
            user = user,
            drivingLicenseNumber = "OLDLICENSE",
            drivingLicenseExpiry = LocalDate.now().plusYears(1),
            preferences = mutableSetOf(Preference.EMAIL_NOTIFICATIONS),
            reliabilityScore = 0f
        )

        val updateRequest = createValidProfileRequest()

        customerProfileService.updateExistingProfile(existingProfile, updateRequest)

        assertEquals(updateRequest.drivingLicenseNumber, existingProfile.drivingLicenseNumber)
        assertEquals(updateRequest.drivingLicenseExpiry, existingProfile.drivingLicenseExpiry)
        assertEquals(updateRequest.preferences, existingProfile.preferences)
    }

    @Test
    fun `updateExistingProfile should throw InvalidUserException for expired license`() {
        val user = createMockUser()
        val existingProfile = CustomerProfile(
            user = user,
            drivingLicenseNumber = "VALID123",
            drivingLicenseExpiry = LocalDate.now().plusYears(1),
            preferences = mutableSetOf(Preference.SMS_NOTIFICATIONS),
            reliabilityScore = 0f
        )

        val badUpdateRequest = createValidProfileRequest().copy(drivingLicenseExpiry = LocalDate.now().minusDays(1))

        val exception = assertThrows<InvalidUserException> {
            customerProfileService.updateExistingProfile(existingProfile, badUpdateRequest)
        }

        assertTrue(exception.message!!.contains("Driving license must not be expired"))
    }

    @Test
    fun `deleteProfile should call repository delete`() {
        val profile = mock(CustomerProfile::class.java)

        customerProfileService.deleteProfile(profile)

        verify(customerProfileRepository, times(1)).delete(profile)
    }
}
