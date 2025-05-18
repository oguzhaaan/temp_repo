package g11.reservationservice.services

import g11.reservationservice.dtos.UserDTO

interface UserClient {
    fun getUserById(userId: Long): UserDTO
}