package g11.usermanagementservice.repositories

import g11.usermanagementservice.entities.CustomerProfile
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerProfileRepository
    : JpaRepository<CustomerProfile, Long>