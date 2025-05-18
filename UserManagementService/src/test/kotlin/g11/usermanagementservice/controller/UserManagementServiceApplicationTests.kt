package g11.usermanagementservice.controllers
import g11.usermanagementservice.dtos.UserRequestDTO
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule

import g11.usermanagementservice.dtos.UserResponseDTO
import g11.usermanagementservice.entities.enumerations.Role
import g11.usermanagementservice.services.UserService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate

@WebMvcTest(UserController::class)
class UserControllerIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var userService: UserService

    @Test
    fun `getUsers should return all users when no criteria is provided`() {
        val users = listOf(
            UserResponseDTO(
                id = 1L,
                username = "user1",
                firstName = "John",
                lastName = "Doe",
                email = "user1@example.com",
                phoneNumber = "1234567890",
                role = Role.CUSTOMER,
                birthDate = LocalDate.of(1990, 1, 1),
                customerProfile = null
            ),
            UserResponseDTO(
                id = 2L,
                username = "user2",
                firstName = "Jane",
                lastName = "Smith",
                email = "user2@example.com",
                phoneNumber = "0987654321",
                role = Role.MANAGER,
                birthDate = LocalDate.of(1992, 5, 15),
                customerProfile = null
            )
        )

        `when`(userService.getAllUsers()).thenReturn(users)

        mockMvc.perform(get("/api/v1/users").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.size()").value(users.size))
            .andExpect(jsonPath("$[0].username").value("user1"))
            .andExpect(jsonPath("$[0].firstName").value("John"))
            .andExpect(jsonPath("$[1].username").value("user2"))
            .andExpect(jsonPath("$[1].firstName").value("Jane"))
    }

    @Test
    fun `getUsers should return users matching username criteria`() {
        val users = listOf(
            UserResponseDTO(
                id = 1L,
                username = "user1",
                firstName = "John",
                lastName = "Doe",
                email = "user1@example.com",
                phoneNumber = "1234567890",
                role = Role.CUSTOMER,
                birthDate = LocalDate.of(1990, 1, 1),
                customerProfile = null
            )
        )

        `when`(userService.searchUsers("user1", null, null)).thenReturn(users)

        mockMvc.perform(
            get("/api/v1/users")
                .param("username", "user1")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.size()").value(1))
            .andExpect(jsonPath("$[0].username").value("user1"))
    }

    @Test
    fun `getUserById should return user when user exists`() {
        val userId = 1L
        val user = UserResponseDTO(
            id = userId,
            username = "user1",
            firstName = "John",
            lastName = "Doe",
            email = "user1@example.com",
            phoneNumber = "1234567890",
            role = Role.CUSTOMER,
            birthDate = LocalDate.of(1990, 1, 1),
            customerProfile = null
        )

        `when`(userService.getUserById(userId)).thenReturn(user)

        mockMvc.perform(
            get("/api/v1/users/{id}", userId)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.username").value("user1"))
            .andExpect(jsonPath("$.firstName").value("John"))
            .andExpect(jsonPath("$.lastName").value("Doe"))
            .andExpect(jsonPath("$.email").value("user1@example.com"))
            .andExpect(jsonPath("$.phoneNumber").value("1234567890"))
            .andExpect(jsonPath("$.role").value("CUSTOMER"))
    }

    @Test
    fun `getUserById should return 404 when user does not exist`() {
        val nonExistentUserId = 999L

        `when`(userService.getUserById(nonExistentUserId))
            .thenThrow(g11.usermanagementservice.exceptions.UserNotFoundException(nonExistentUserId))

        mockMvc.perform(
            get("/api/v1/users/{id}", nonExistentUserId)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.title").value("User Not Found"))
            .andExpect(jsonPath("$.status").value(404))
    }

    @Test
    fun `createUser should return 201 when user is successfully created`() {
        val objectMapper = jacksonObjectMapper().apply {
            registerModule(JavaTimeModule())
        }

        val userDto = UserRequestDTO(
            username = "newuser",
            firstName = "Alice",
            lastName = "Wonderland",
            email = "alice@example.com",
            phoneNumber = "1234567890",
            birthDate = LocalDate.of(1995, 8, 25),
            customerProfile = null
        )

        val createdUser = UserResponseDTO(
            id = 1L,
            username = "newuser",
            firstName = "Alice",
            lastName = "Wonderland",
            email = "alice@example.com",
            phoneNumber = "1234567890",
            role = Role.CUSTOMER,
            birthDate = LocalDate.of(1995, 8, 25),
            customerProfile = null
        )

        `when`(userService.createUser(userDto, Role.CUSTOMER)).thenReturn(createdUser)

        mockMvc.perform(
            post("/api/v1/users")
                .param("role", "CUSTOMER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto))
        )
            .andExpect(status().isCreated)
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.username").value("newuser"))
            .andExpect(jsonPath("$.email").value("alice@example.com"))
    }

    @Test
    fun `createUser should return 409 Conflict when username or email already exists`() {
        val objectMapper = jacksonObjectMapper().apply {
            registerModule(JavaTimeModule())
        }

        val userDto = UserRequestDTO(
            username = "existinguser",
            firstName = "Bob",
            lastName = "Builder",
            email = "existing@example.com",
            phoneNumber = "1122334455",
            birthDate = LocalDate.of(1990, 2, 20),
            customerProfile = null
        )

        `when`(userService.createUser(userDto, Role.CUSTOMER))
            .thenThrow(g11.usermanagementservice.exceptions.EmailExistsException("Email already exists"))

        mockMvc.perform(
            post("/api/v1/users")
                .param("role", "CUSTOMER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto))
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.title").value("Email Conflict"))
            .andExpect(jsonPath("$.status").value(409))
    }

    @Test
    fun `updateUserInfo should return 200 when user is successfully updated`() {
        val objectMapper = jacksonObjectMapper().apply {
            registerModule(JavaTimeModule())
        }

        val userDto = UserRequestDTO(
            username = "updateduser",
            firstName = "Updated",
            lastName = "User",
            email = "updated@example.com",
            phoneNumber = "2233445566",
            birthDate = LocalDate.of(1992, 3, 10),
            customerProfile = null
        )

        val updatedUser = UserResponseDTO(
            id = 1L,
            username = "updateduser",
            firstName = "Updated",
            lastName = "User",
            email = "updated@example.com",
            phoneNumber = "2233445566",
            role = Role.CUSTOMER,
            birthDate = LocalDate.of(1992, 3, 10),
            customerProfile = null
        )

        `when`(userService.updateUserInfo(1L, userDto)).thenReturn(updatedUser)

        mockMvc.perform(
            put("/api/v1/users/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.username").value("updateduser"))
            .andExpect(jsonPath("$.email").value("updated@example.com"))
    }

    @Test
    fun `updateUserInfo should return 404 when user is not found`() {
        val objectMapper = jacksonObjectMapper().apply {
            registerModule(JavaTimeModule())
        }

        val userDto = UserRequestDTO(
            username = "nonexistent",
            firstName = "Ghost",
            lastName = "User",
            email = "ghost@example.com",
            phoneNumber = "0000000000",
            birthDate = LocalDate.of(1980, 1, 1),
            customerProfile = null
        )

        `when`(userService.updateUserInfo(99L, userDto))
            .thenThrow(g11.usermanagementservice.exceptions.UserNotFoundException(999L))

        mockMvc.perform(
            put("/api/v1/users/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto))
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.title").value("User Not Found"))
            .andExpect(jsonPath("$.status").value(404))
    }

    @Test
    fun `updateUserInfo should return 409 when username or email already exists`() {
        val objectMapper = jacksonObjectMapper().apply {
            registerModule(JavaTimeModule())
        }

        val userDto = UserRequestDTO(
            username = "existingusername",
            firstName = "Conflict",
            lastName = "User",
            email = "conflict@example.com",
            phoneNumber = "1122334455",
            birthDate = LocalDate.of(1985, 6, 15),
            customerProfile = null
        )

        `when`(userService.updateUserInfo(1L, userDto))
            .thenThrow(g11.usermanagementservice.exceptions.UsernameExistsException(username = "existingusername"))

        mockMvc.perform(
            put("/api/v1/users/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto))
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.title").value("Username Conflict"))
            .andExpect(jsonPath("$.status").value(409))
    }

    @Test
    fun `deleteUser should return 204 when user is successfully deleted`() {
        mockMvc.perform(
            delete("/api/v1/users/{id}", 1L)
        )
            .andExpect(status().isNoContent)
    }

    @Test
    fun `deleteUser should return 404 when user is not found`() {
        `when`(userService.deleteUser(99L))
            .thenThrow(g11.usermanagementservice.exceptions.UserNotFoundException(99L))

        mockMvc.perform(
            delete("/api/v1/users/{id}", 99L)
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.title").value("User Not Found"))
            .andExpect(jsonPath("$.status").value(404))
    }

    @Test
    fun `changeUserRole should return 200 when role is successfully changed`() {
        val updatedUser = UserResponseDTO(
            id = 1L,
            username = "user1",
            firstName = "John",
            lastName = "Doe",
            email = "user1@example.com",
            phoneNumber = "1234567890",
            role = Role.MANAGER,  // changed role
            birthDate = LocalDate.of(1990, 1, 1),
            customerProfile = null
        )

        `when`(userService.changeUserRole(1L, Role.MANAGER)).thenReturn(updatedUser)

        mockMvc.perform(
            put("/api/v1/users/{id}/role", 1L)
                .param("role", "MANAGER")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.role").value("MANAGER"))
    }
    @Test
    fun `getRoleById should return 200 with user's role`() {
        `when`(userService.getRoleById(1L)).thenReturn(Role.CUSTOMER)

        mockMvc.perform(
            get("/api/v1/users/{id}/role", 1L)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().json("\"CUSTOMER\""))
    }


}
