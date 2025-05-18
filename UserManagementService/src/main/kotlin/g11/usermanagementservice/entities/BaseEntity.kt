package g11.usermanagementservice.entities

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.springframework.data.util.ProxyUtils
import java.io.Serializable

@MappedSuperclass
abstract class BaseEntity<T : Serializable> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private var id: T = 0 as T

    fun getId(): T = id
    override fun toString(): String {
        return "@Entity ${this.javaClass.name}(id=$id)"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other === this) return true
        if (javaClass != ProxyUtils.getUserClass(other))
            return false
        other as BaseEntity<*>
        return if (null == id) false
        else this.id == other.id
    }

    override fun hashCode(): Int {
        return 31 //any value will do
    }
}
