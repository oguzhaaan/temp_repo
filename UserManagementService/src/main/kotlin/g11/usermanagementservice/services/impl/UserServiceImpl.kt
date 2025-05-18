package g11.usermanagementservice.services.impl


import g11.usermanagementservice.dtos.UserRequestDTO
import g11.usermanagementservice.dtos.UserResponseDTO
import g11.usermanagementservice.entities.User
import g11.usermanagementservice.entities.enumerations.Role
import g11.usermanagementservice.exceptions.UsernameExistsException
import g11.usermanagementservice.exceptions.EmailExistsException
import g11.usermanagementservice.exceptions.InvalidUserException
import g11.usermanagementservice.exceptions.UserNotFoundException
import g11.usermanagementservice.mappers.toDto
import g11.usermanagementservice.mappers.toEntity
import g11.usermanagementservice.repositories.UserRepository
import g11.usermanagementservice.services.CustomerProfileService
import g11.usermanagementservice.services.UserService
import g11.usermanagementservice.specifications.UserSpecifications
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.Period

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val customerProfileService: CustomerProfileService,
) : UserService {

    private val logger = LoggerFactory.getLogger(UserServiceImpl::class.java)

    @Transactional(readOnly = true)
    override fun getAllUsers(): List<UserResponseDTO> =
        userRepository.findAll().map { it.toDto( it.customerProfile?.toDto() ) }

    @Transactional(readOnly = true)
    override fun getUserById(id: Long): UserResponseDTO {
        val user = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("User with id $id not found") }
        return user.toDto(user.customerProfile?.toDto())
    }

    @Transactional(readOnly = true)
    override fun getUsersByRole(role: Role): List<UserResponseDTO> {
        val users = userRepository.findByRole(role)
        return users.map { it.toDto(it.customerProfile?.toDto()) }
    }

    @Transactional
    override fun createUser(userDto: UserRequestDTO, role: Role): UserResponseDTO {
        if (userRepository.existsByUsername(userDto.username)) {
            throw UsernameExistsException(userDto.username)
        }
        if (userRepository.existsByEmail(userDto.email)) {
            throw EmailExistsException(userDto.email)
        }

        val user = userDto.toEntity(role)

        if (role == Role.CUSTOMER) {
            userDto.customerProfile?.let {
                val profileEntity = customerProfileService.createProfileEntity(it, user)
                user.customerProfile = profileEntity
            }
        } else if (userDto.customerProfile != null) {
            throw IllegalStateException("Only customers can have a customer profile.")
        }

        validateUserData(user)

        val savedUser = userRepository.save(user) // saves both user and profile due to cascade
        logger.info("New user added: ${savedUser.getId()}, username=${savedUser.username}")
        return savedUser.toDto(savedUser.customerProfile?.toDto())
    }

    @Transactional
    override fun updateUserInfo(id: Long, userDto: UserRequestDTO): UserResponseDTO {
        val user = userRepository.findById(id).orElseThrow { UserNotFoundException(id) }

        val usernameConflict = userRepository.findByUsername(userDto.username)?.let { it.getId() != id }
        if (usernameConflict == true) throw UsernameExistsException(userDto.username)

        val emailConflict = userRepository.findByEmail(userDto.email)?.let { it.getId() != id }
        if (emailConflict == true) throw EmailExistsException(userDto.email)

        user.apply {
            username = userDto.username
            email = userDto.email
            phoneNumber = userDto.phoneNumber
            firstName = userDto.firstName
            lastName = userDto.lastName
            birthDate = userDto.birthDate
        }

        if (user.role == Role.CUSTOMER) {
            userDto.customerProfile?.let { dto ->
                if (user.customerProfile != null) {
                    customerProfileService.updateExistingProfile(user.customerProfile!!, dto)
                } else {
                    user.customerProfile = customerProfileService.createProfileEntity(dto, user)
                }
            }
        } else if (userDto.customerProfile != null) {
            throw IllegalStateException("Only customers can have a customer profile.")
        }

        validateUserData(user)

        val updatedUser = userRepository.save(user) // saves both due to cascade
        logger.info("User with id ${updatedUser.getId()} updated successfully.")
        return updatedUser.toDto(updatedUser.customerProfile?.toDto())
    }



    @Transactional
    override fun deleteUser(id: Long) {
        val user = userRepository.findById(id)
            .orElseThrow { UserNotFoundException(id) }
        userRepository.delete(user)
        logger.info("User with id $id deleted successfully.")
    }

    @Transactional(readOnly = true)
    override fun getUserByUsername(username: String): UserResponseDTO {
        val user = userRepository.findByUsername(username)
            ?: throw UserNotFoundException(0)
        return user.toDto(user.customerProfile?.toDto())
    }

    @Transactional(readOnly = true)
    override fun getUserByEmail(email: String): UserResponseDTO {
        val user = userRepository.findByEmail(email)
            ?: throw UserNotFoundException(0)
        return user.toDto(user.customerProfile?.toDto())
    }

    override fun changeUserRole(id: Long, role: Role): UserResponseDTO {
        val user = userRepository.findById(id)
            .orElseThrow { UserNotFoundException(id) }
        if (role != Role.CUSTOMER && user.customerProfile != null) {
            customerProfileService.deleteProfile(user.customerProfile!!)
            user.customerProfile = null
        }
        user.role = role
        val updatedUser = userRepository.save(user)
        return updatedUser.toDto(updatedUser.customerProfile?.toDto())
    }

    @Transactional(readOnly = true)
    override fun getRoleById(id: Long): Role {
        return userRepository.findRoleById(id)
    }

    private fun validateUserData(user: User) {
        if (user.birthDate == null) {
            throw InvalidUserException("Birth date is required.")
        }

        val age = Period.between(user.birthDate, LocalDate.now()).years
        if (age < 18) {
            throw InvalidUserException("User must be at least 18 years old.")
        }
    }

    @Transactional(readOnly = true)
    override fun searchUsers(username: String?, email: String?, role: Role?): List<UserResponseDTO> {
        val spec = Specification.where<User>(null)
            .and(username?.let { UserSpecifications.usernameEquals(it) })
            .and(email?.let { UserSpecifications.emailEquals(it) })
            .and(role?.let { UserSpecifications.roleEquals(it) })

        return userRepository.findAll(spec)
            .map { it.toDto(it.customerProfile?.toDto()) }
    }


}