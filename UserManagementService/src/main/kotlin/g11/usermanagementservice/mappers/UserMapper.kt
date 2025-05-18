package g11.usermanagementservice.mappers

import g11.usermanagementservice.dtos.CustomerProfileResponseDTO
import g11.usermanagementservice.dtos.UserRequestDTO
import g11.usermanagementservice.dtos.UserResponseDTO
import g11.usermanagementservice.entities.User
import g11.usermanagementservice.entities.enumerations.Role

fun User.toDto(customerProfile: CustomerProfileResponseDTO?): UserResponseDTO {
    return UserResponseDTO(
        id = this.getId(),
        username = this.username,
        email = this.email,
        firstName = this.firstName,
        lastName = this.lastName,
        role = this.role,
        phoneNumber = this.phoneNumber,
        birthDate = this.birthDate,
        customerProfile = customerProfile
    )
}

fun UserRequestDTO.toEntity(role: Role): User {
    return User(
        username = this.username,
        email = this.email,
        firstName = this.firstName,
        lastName = this.lastName,
        phoneNumber = this.phoneNumber,
        birthDate = this.birthDate,
        role = role
    )
}