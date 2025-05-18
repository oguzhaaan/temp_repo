package g11.usermanagementservice.controllers

import g11.usermanagementservice.dtos.UserRequestDTO
import g11.usermanagementservice.dtos.UserResponseDTO
import g11.usermanagementservice.entities.enumerations.Role
import g11.usermanagementservice.services.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@Tag(name = "User Management", description = "Endpoints for managing users")
@RestController
@RequestMapping("api/v1/users")
class UserController(private val userService: UserService) {

    @Operation(summary = "Get all users or search by criteria")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        ApiResponse(responseCode = "400", description = "Invalid search parameters")
    )
    @GetMapping
    fun getUsers(
        @RequestParam(required = false) username: String?,
        @RequestParam(required = false) email: String?,
        @RequestParam(required = false) role: Role?
    ): ResponseEntity<List<UserResponseDTO>> {
        val results = if (username != null || email != null || role != null) {
            userService.searchUsers(username, email, role)
        } else {
            userService.getAllUsers()
        }
        return ResponseEntity.ok(results)
    }


    @Operation(summary = "Get user by ID")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "User retrieved successfully"),
        ApiResponse(responseCode = "404", description = "User not found")
    )
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<UserResponseDTO> =
        ResponseEntity.ok(userService.getUserById(id))

    @Operation(summary = "Create a new user")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "User created successfully"),
        ApiResponse(responseCode = "400", description = "Invalid input data"),
        ApiResponse(responseCode = "409", description = "Username or email already exists")
    )
    @PostMapping
    fun createUser(
        @Valid @RequestBody userDto: UserRequestDTO,
        @RequestParam role: Role,
        uriBuilder: UriComponentsBuilder
    ): ResponseEntity<UserResponseDTO> {
        val createdUser = userService.createUser(userDto, role)
        val location = uriBuilder.path("/users/{id}").buildAndExpand(createdUser.id).toUri()
        return ResponseEntity.created(location).body(createdUser)
    }

    @Operation(summary = "Update user information")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "User updated successfully"),
        ApiResponse(responseCode = "400", description = "Invalid input data"),
        ApiResponse(responseCode = "404", description = "User not found"),
        ApiResponse(responseCode = "409", description = "Username or email already exists")
    )
    @PutMapping("/{id}")
    fun updateUserInfo(
        @PathVariable id: Long,
        @Valid @RequestBody userDto: UserRequestDTO
    ): ResponseEntity<UserResponseDTO> =
        ResponseEntity.ok(userService.updateUserInfo(id, userDto))

    @Operation(summary = "Delete a user")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "User deleted successfully"),
        ApiResponse(responseCode = "404", description = "User not found")
    )
    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        userService.deleteUser(id)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "Change user role")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "User role changed successfully"),
        ApiResponse(responseCode = "400", description = "Invalid role"),
        ApiResponse(responseCode = "404", description = "User not found")
    )
    @PutMapping("/{id}/role")
    fun changeUserRole(
        @PathVariable id: Long,
        @RequestParam role: Role
    ): ResponseEntity<UserResponseDTO> =
        ResponseEntity.ok(userService.changeUserRole(id, role))

    @Operation(summary = "Get role of the user having user ID")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Role retrieved successfully"),
        ApiResponse(responseCode = "404", description = "Role not found")
    )
    @GetMapping("/{id}/role")
    fun getRoleById(@PathVariable id: Long): ResponseEntity<Role> =
        ResponseEntity.ok(userService.getRoleById(id))
}
