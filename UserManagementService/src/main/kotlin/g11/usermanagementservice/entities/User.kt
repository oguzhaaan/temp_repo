package g11.usermanagementservice.entities

import g11.usermanagementservice.entities.enumerations.Role
import jakarta.persistence.Entity
import jakarta.persistence.Table

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "users")
class User(

    @Column(nullable = false, unique = true)
    var username: String,

    @Column(nullable = false)
    var firstName: String,

    @Column(nullable = false)
    var lastName: String,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var phoneNumber: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: Role,

    @Column(nullable = false)
    var birthDate: LocalDate,

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, optional = true)
    var customerProfile: CustomerProfile? = null

) : BaseEntity<Long>() {

    @PrePersist
    @PreUpdate
    fun validate() {
        if (role != Role.CUSTOMER && customerProfile != null) {
            throw IllegalStateException("Only customers can have a customer profile.")
        }
    }
}




