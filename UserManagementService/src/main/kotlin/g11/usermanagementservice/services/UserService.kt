package g11.usermanagementservice.services


import g11.usermanagementservice.dtos.UserRequestDTO
import g11.usermanagementservice.dtos.UserResponseDTO
import g11.usermanagementservice.entities.enumerations.Role

interface UserService {
    fun getAllUsers(): List<UserResponseDTO>
    fun getUserById(id: Long): UserResponseDTO
    fun getUsersByRole(role: Role): List<UserResponseDTO>
    fun createUser(userDto: UserRequestDTO, role: Role): UserResponseDTO
    fun updateUserInfo(id: Long, userDto: UserRequestDTO): UserResponseDTO
    fun deleteUser(id: Long)
    fun getUserByUsername(username: String): UserResponseDTO
    fun getUserByEmail(email: String): UserResponseDTO
    fun changeUserRole(id: Long, role: Role): UserResponseDTO
    fun getRoleById(id: Long): Role
    fun searchUsers(username: String?, email: String?, role: Role?): List<UserResponseDTO>
}