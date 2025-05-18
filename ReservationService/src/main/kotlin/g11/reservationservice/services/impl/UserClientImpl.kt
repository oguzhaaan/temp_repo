package g11.reservationservice.services.impl

import g11.reservationservice.dtos.UserDTO
import g11.reservationservice.exceptions.ApiException
import g11.reservationservice.exceptions.UserNotFoundException
import g11.reservationservice.services.UserClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClient

@Service
class UserClientImpl(
    @Qualifier("userRestClient") private val restClient: RestClient
) : UserClient {

    override fun getUserById(userId: Long): UserDTO {
        return try {
            restClient.get()
                .uri("/{id}", userId)
                .retrieve()
                .onStatus({ status -> status == HttpStatus.NOT_FOUND }) { _, _ ->
                    throw UserNotFoundException(userId)
                }
                .body(UserDTO::class.java)!!
        } catch (ex: HttpClientErrorException) {
            if (ex.statusCode == HttpStatus.BAD_REQUEST) {
                throw UserNotFoundException(userId)
            }
            throw ApiException("User service error: ${ex.statusCode} - ${ex.responseBodyAsString}")
        }
    }

}
