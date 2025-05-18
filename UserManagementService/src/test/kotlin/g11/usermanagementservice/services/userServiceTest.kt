package g11.usermanagementservice.services


import g11.usermanagementservice.dtos.UserRequestDTO
import g11.usermanagementservice.dtos.UserResponseDTO
import g11.usermanagementservice.entities.User
import g11.usermanagementservice.entities.enumerations.Role
import g11.usermanagementservice.exceptions.EmailExistsException
import g11.usermanagementservice.exceptions.UserNotFoundException
import g11.usermanagementservice.exceptions.UsernameExistsException
import g11.usermanagementservice.mappers.toEntity
import g11.usermanagementservice.repositories.UserRepository
import g11.usermanagementservice.services.impl.UserServiceImpl
import net.bytebuddy.matcher.ElementMatchers.any
import org.hamcrest.CoreMatchers.any
import org.mockito.Mockito.`when`

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate
import java.util.Optional
import kotlin.collections.emptyList

@ExtendWith(MockitoExtension::class)
class UserServiceImplTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var customerProfileService: CustomerProfileService

    @InjectMocks
    private lateinit var userService: UserServiceImpl

    @Test
    fun `getAllUsers should return list of UserResponseDTO`() {
        // Arrange
        val users = listOf(
            User(
                username = "user1",
                firstName = "John",
                lastName = "Doe",
                email = "user1@example.com",
                phoneNumber = "1234567890",
                role = Role.CUSTOMER,
                birthDate = LocalDate.of(1990, 1, 1)
            ),
            User(
                username = "user2",
                firstName = "Jane",
                lastName = "Smith",
                email = "user2@example.com",
                phoneNumber = "0987654321",
                role = Role.MANAGER,
                birthDate = LocalDate.of(1995, 5, 15)
            )
        )

        `when`(userRepository.findAll()).thenReturn(users)

        // Act
        val result = userService.getAllUsers()

        // Assert
        assertEquals(2, result.size)
        assertEquals("user1", result[0].username)
        assertEquals("user2", result[1].username)
    }

    @Test
    fun `getUserById should return UserResponseDTO when user exists`() {
        // Arrange
        val user = User(
            username = "user1",
            firstName = "John",
            lastName = "Doe",
            email = "user1@example.com",
            phoneNumber = "1234567890",
            role = Role.CUSTOMER,
            birthDate = LocalDate.of(1990, 1, 1)
        )
        `when`(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user))

        // Act
        val result = userService.getUserById(1L)

        // Assert
        assertEquals("user1", result.username)
        assertEquals("John", result.firstName)
        assertEquals("Doe", result.lastName)
        assertEquals("user1@example.com", result.email)
    }
    @Test
    fun `getUserById should throw exception when user does not exist`() {
        `when`(userRepository.findById(99L)).thenReturn(java.util.Optional.empty())

        val exception = assertThrows(IllegalArgumentException::class.java) {
            userService.getUserById(99L)
        }
        println("Actual exception message: ${exception.message}")
        assertEquals("User with id 99 not found", exception.message)
    }

    @Test
    fun `getUsersByRole should return list of users with specified role`() {
        // Arrange
        val role = Role.CUSTOMER
        val users = listOf(
            User(
                username = "customer1",
                firstName = "First",
                lastName = "Customer",
                email = "customer1@example.com",
                phoneNumber = "123456789",
                role = role,
                birthDate = LocalDate.of(1990, 1, 1)
            )
        )

        `when`(userRepository.findByRole(role)).thenReturn(users)

        // Act
        val result = userService.getUsersByRole(role)

        // Assert
        assertEquals(1, result.size)
        assertEquals("customer1", result[0].username)
        assertEquals(Role.CUSTOMER, result[0].role)
    }

    @Test
    fun `createUser should successfully create a new user when valid data provided`() {
        // Arrange
        val userRequest = UserRequestDTO(
            username = "newuser",
            firstName = "New",
            lastName = "User",
            email = "newuser@example.com",
            phoneNumber = "1234567890",
            birthDate = LocalDate.of(2000, 1, 1),
            customerProfile = null
        )
        val role = Role.CUSTOMER

        val userEntity = userRequest.toEntity(role)
        val savedUser = User(
            username = userEntity.username,
            firstName = userEntity.firstName,
            lastName = userEntity.lastName,
            email = userEntity.email,
            phoneNumber = userEntity.phoneNumber,
            role = userEntity.role,
            birthDate = userEntity.birthDate
        )

        `when`(userRepository.existsByUsername(userRequest.username)).thenReturn(false)
        `when`(userRepository.existsByEmail(userRequest.email)).thenReturn(false)
        `when`(userRepository.save(Mockito.any(User::class.java))).thenReturn(savedUser)

        // Act
        val result = userService.createUser(userRequest, role)

        // Assert
        assertEquals(userRequest.username, result.username)
        assertEquals(userRequest.email, result.email)
        assertEquals(role, result.role)
    }

    @Test
    fun `getUserByEmail should return UserResponseDTO when email exists`() {
        // Arrange
        val email = "john@example.com"
        val user = User(
            username = "testuser",
            firstName = "First",
            lastName = "Last",
            email = "john@example.com",
            phoneNumber = "1234567890",
            role = Role.CUSTOMER,
            birthDate = LocalDate.of(1990, 1, 1)
        )
        `when`(userRepository.findByEmail(email)).thenReturn(user)

        // Act
        val result = userService.getUserByEmail(email)

        // Assert
        assertEquals(email, result.email)
    }

    @Test
    fun `getUserByEmail should throw exception when user does not exist`() {
        // Arrange
        val email = "nonexistent@example.com"
        Mockito.`when`(userRepository.findByEmail(email)).thenReturn(null)

        // Act & Assert
        assertThrows(UserNotFoundException::class.java) {
            userService.getUserByEmail(email)
        }
    }
    @Test
    fun `changeUserRole should update user role successfully`() {
        // Arrange
        val userId = 1L
        val oldRole = Role.CUSTOMER
        val newRole = Role.MANAGER
        val user = User(
            username = "testuser",
            firstName = "Test",
            lastName = "User",
            email = "test@example.com",
            phoneNumber = "1234567890",
            role = oldRole,
            birthDate = LocalDate.of(1995, 5, 15),
            customerProfile = null
        )

        `when`(userRepository.findById(userId)).thenReturn(Optional.of(user))
        `when`(userRepository.save(Mockito.any(User::class.java))).thenReturn(user.apply { role = newRole })

        // Act
        val result = userService.changeUserRole(userId, newRole)

        // Assert
        assertEquals(newRole, result.role)
    }

    @Test
    fun `changeUserRole should throw exception when user not found`() {
        // Arrange
        val userId = 999L
        `when`(userRepository.findById(userId)).thenReturn(Optional.empty())

        // Act & Assert
        assertThrows(UserNotFoundException::class.java) {
            userService.changeUserRole(userId, Role.MANAGER)
        }
    }

    @Test
    fun `getRoleById should return the role when user exists`() {
        // Arrange
        val userId = 1L
        val role = Role.CUSTOMER

        Mockito.`when`(userRepository.findRoleById(userId)).thenReturn(role)

        // Act
        val result = userService.getRoleById(userId)

        // Assert
        assertEquals(role, result)
    }
    @Test
    fun `getRoleById should throw exception when user not found`() {
        // Arrange
        val userId = 99L
        Mockito.`when`(userRepository.findRoleById(userId)).thenThrow(NoSuchElementException("User not found"))

        // Act & Assert
        assertThrows<NoSuchElementException> {
            userService.getRoleById(userId)
        }
    }

    @Test
    fun `updateUserInfo should update user information successfully`() {
        val id = 1L
        val existingUser = User(
            username = "oldUsername",
            firstName = "Old",
            lastName = "Name",
            email = "old@example.com",
            phoneNumber = "1234567890",
            role = Role.CUSTOMER,
            birthDate = LocalDate.of(1990, 1, 1),
            customerProfile = null
        )

        val updateDto = UserRequestDTO(
            username = "newUsername",
            firstName = "New",
            lastName = "Name",
            email = "new@example.com",
            phoneNumber = "0987654321",
            birthDate = LocalDate.of(1990, 1, 1),
            customerProfile = null
        )

        `when`(userRepository.findById(id)).thenReturn(Optional.of(existingUser))
        `when`(userRepository.findByUsername("newUsername")).thenReturn(null)
        `when`(userRepository.findByEmail("new@example.com")).thenReturn(null)
        `when`(userRepository.save(Mockito.any(User::class.java))).thenReturn(existingUser)

        val updatedUser = userService.updateUserInfo(id, updateDto)


        assertEquals(updateDto.username, updatedUser.username)
        assertEquals(updateDto.email, updatedUser.email)
    }

    @Test
    fun `updateUserInfo should throw UsernameExistsException when username already exists`() {
        // Arrange
        val existingUser = User(
            username = "olduser",
            firstName = "Old",
            lastName = "User",
            email = "old@example.com",
            phoneNumber = "1234567890",
            role = Role.CUSTOMER,
            birthDate = LocalDate.of(1990, 1, 1)
        )

        val conflictingUser = User(
            username = "newuser",
            firstName = "Conflict",
            lastName = "User",
            email = "conflict@example.com",
            phoneNumber = "0987654321",
            role = Role.CUSTOMER,
            birthDate = LocalDate.of(1995, 5, 5)
        )

        val userDto = UserRequestDTO(
            username = "newuser",
            firstName = "Updated",
            lastName = "User",
            email = "updated@example.com",
            phoneNumber = "1234509876",
            birthDate = LocalDate.of(1995, 5, 5),
            customerProfile = null
        )

        `when`(userRepository.findById(anyLong())).thenReturn(Optional.of(existingUser))
        `when`(userRepository.findByUsername("newuser")).thenReturn(conflictingUser)

        // Act & Assert
        assertThrows(UsernameExistsException::class.java) {
            userService.updateUserInfo(1L, userDto)
        }

        verify(userRepository, times(1)).findById(1L)
        verify(userRepository, times(1)).findByUsername("newuser")
    }

    @Test
    fun `updateUserInfo should throw EmailExistsException when email already exists`() {
        // Arrange
        val existingUser = User(
            username = "olduser",
            firstName = "Old",
            lastName = "User",
            email = "old@example.com",
            phoneNumber = "1234567890",
            role = Role.CUSTOMER,
            birthDate = LocalDate.of(1990, 1, 1)
        )

        val conflictingUser = User(
            username = "conflictuser",
            firstName = "Conflict",
            lastName = "User",
            email = "updated@example.com", // Email we will conflict with
            phoneNumber = "0987654321",
            role = Role.CUSTOMER,
            birthDate = LocalDate.of(1995, 5, 5)
        )

        val userDto = UserRequestDTO(
            username = "newusername",
            firstName = "Updated",
            lastName = "User",
            email = "updated@example.com", // trying to update to an existing email
            phoneNumber = "1234509876",
            birthDate = LocalDate.of(1995, 5, 5),
            customerProfile = null
        )

        `when`(userRepository.findById(anyLong())).thenReturn(Optional.of(existingUser))
        `when`(userRepository.findByUsername("newusername")).thenReturn(null) // No username conflict
        `when`(userRepository.findByEmail("updated@example.com")).thenReturn(conflictingUser) // Email conflict

        // Act & Assert
        assertThrows(EmailExistsException::class.java) {
            userService.updateUserInfo(1L, userDto)
        }

        verify(userRepository, times(1)).findById(1L)
        verify(userRepository, times(1)).findByUsername("newusername")
        verify(userRepository, times(1)).findByEmail("updated@example.com")
    }

    @Test
    fun `deleteUser should delete user when user exists`() {
        // Arrange
        val user = User(
            username = "userToDelete",
            firstName = "First",
            lastName = "Last",
            email = "user@example.com",
            phoneNumber = "1234567890",
            role = Role.CUSTOMER,
            birthDate = LocalDate.of(1990, 1, 1)
        ).apply {

        }

        `when`(userRepository.findById(1L)).thenReturn(Optional.of(user))

        // Act
        userService.deleteUser(1L)

        // Assert
        verify(userRepository, times(1)).delete(user)
    }
    @Test
    fun `deleteUser should throw UserNotFoundException when user does not exist`() {
        // Arrange
        val nonExistentId = 999L
        `when`(userRepository.findById(nonExistentId)).thenReturn(Optional.empty())

        // Act & Assert
        val exception = assertThrows<UserNotFoundException> {
            userService.deleteUser(nonExistentId)
        }
        assertTrue(exception.message!!.contains(nonExistentId.toString()))
    }

    @Test
    fun `getUserByUsername should return user when user exists`() {
        val user = User(
            username = "testuser",
            firstName = "First",
            lastName = "Last",
            email = "user@example.com",
            phoneNumber = "1234567890",
            role = Role.CUSTOMER,
            birthDate = LocalDate.of(1990, 1, 1)
        )
        `when`(userRepository.findByUsername(user.username)).thenReturn(user)

        val result = userService.getUserByUsername(user.username)

        assertEquals(user.username, result.username)
        assertEquals(user.email, result.email)
    }

    @Test
    fun `getUserByUsername should throw exception when user does not exist`() {
        `when`(userRepository.findByUsername("nonexistent")).thenReturn(null)

        assertThrows(UserNotFoundException::class.java) {
            userService.getUserByUsername("nonexistent")
        }
    }


    @Test
    fun `searchUsers should return list of users matching criteria`() {
        // Arrange
        val user = User(
            username = "john_doe",
            firstName = "John",
            lastName = "Doe",
            email = "john@example.com",
            phoneNumber = "1234567890",
            role = Role.CUSTOMER,
            birthDate = LocalDate.of(1995, 1, 1)
        )
        val users = listOf(user)

        `when`(userRepository.findAll(Mockito.any<Specification<User>>())).thenReturn(users)

        // Act
        val result = userService.searchUsers(username = "john_doe", email = null, role = null)

        // Assert
        assertEquals(1, result.size)
        assertEquals("john_doe", result[0].username)
    }





}
