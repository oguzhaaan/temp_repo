package g11.usermanagementservice.repositories

import g11.usermanagementservice.entities.User
import g11.usermanagementservice.entities.enumerations.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor


interface UserRepository
    : JpaRepository<User, Long>,
    JpaSpecificationExecutor<User> {
        fun findByRole(role: Role): List<User>
        fun existsByUsername(username: String): Boolean
        fun existsByEmail(email: String): Boolean
        fun findByUsername(username: String): User?
        fun findByEmail(email: String): User?
        fun findRoleById(id: Long): Role
    }
