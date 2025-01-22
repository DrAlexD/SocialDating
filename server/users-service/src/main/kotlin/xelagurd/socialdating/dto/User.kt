package xelagurd.socialdating.dto

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id


@Entity(name = "users")
class User(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,
    var name: String? = null,
    var gender: Gender? = null,
    var username: String? = null,
    var password: String? = null,
    var email: String? = null,
    var age: Int? = null,
    var city: String? = null,
    var purpose: Purpose? = null,
    var activity: Int? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (age != other.age) return false
        if (activity != other.activity) return false
        if (name != other.name) return false
        if (gender != other.gender) return false
        if (username != other.username) return false
        if (password != other.password) return false
        if (email != other.email) return false
        if (city != other.city) return false
        if (purpose != other.purpose) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + (age ?: 0)
        result = 31 * result + (activity ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (gender?.hashCode() ?: 0)
        result = 31 * result + (username?.hashCode() ?: 0)
        result = 31 * result + (password?.hashCode() ?: 0)
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (city?.hashCode() ?: 0)
        result = 31 * result + (purpose?.hashCode() ?: 0)
        return result
    }
}