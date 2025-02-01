package xelagurd.socialdating.server.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import xelagurd.socialdating.server.model.enums.Gender
import xelagurd.socialdating.server.model.enums.Purpose


@Entity(name = "users")
class User(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,
    var name: String,
    var gender: Gender,
    var username: String,
    var password: String,
    var email: String,
    var age: Int,
    var city: String,
    var purpose: Purpose,
    var activity: Int
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
        result = 31 * result + age
        result = 31 * result + activity
        result = 31 * result + name.hashCode()
        result = 31 * result + gender.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + city.hashCode()
        result = 31 * result + purpose.hashCode()
        return result
    }
}