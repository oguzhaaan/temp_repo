package g11.usermanagementservice.specifications

import g11.usermanagementservice.entities.User
import g11.usermanagementservice.entities.enumerations.Role
import org.springframework.data.jpa.domain.Specification

object UserSpecifications {
    fun usernameEquals(username: String): Specification<User> =
        Specification { root, _, cb -> cb.equal(root.get<String>("username"), username) }

    fun emailEquals(email: String): Specification<User> =
        Specification { root, _, cb -> cb.equal(root.get<String>("email"), email) }

    fun roleEquals(role: Role): Specification<User> =
        Specification { root, _, cb -> cb.equal(root.get<Role>("role"), role) }
}
