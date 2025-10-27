package xelagurd.socialdating.server.model

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import xelagurd.socialdating.server.model.enums.Gender
import xelagurd.socialdating.server.model.enums.Purpose
import xelagurd.socialdating.server.model.enums.Role

@Entity(name = "users")
@Table(name = "users")
class User(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int = 0,

    @field:Column(nullable = false)
    @field:NotBlank
    var name: String,

    @field:Enumerated(EnumType.STRING)
    @field:Column(nullable = false)
    var gender: Gender,

    @field:Column(nullable = false, unique = true)
    @field:NotBlank
    @JvmField
    var username: String,

    @field:Column(nullable = false)
    @field:NotBlank
    @JvmField
    var password: String,

    @field:Column
    @field:Email
    var email: String?,

    @field:Column(nullable = false)
    @field:Min(value = 18)
    @field:Max(value = 99)
    var age: Int,

    @field:Column(nullable = false)
    @field:NotBlank
    var city: String,

    @field:Enumerated(EnumType.STRING)
    @field:Column(nullable = false)
    var purpose: Purpose,

    @field:Column(nullable = false)
    @field:Min(value = 0)
    @field:Max(value = 100)
    var activity: Int,

    @field:Enumerated(EnumType.STRING)
    @field:Column(nullable = false)
    val role: Role
) : UserDetails {
    @JsonIgnore
    override fun getAuthorities() = listOf(SimpleGrantedAuthority("ROLE_$role"))

    override fun getUsername() = username
    fun setUsername(username: String) {
        this.username = username
    }

    override fun getPassword() = password
    fun setPassword(password: String) {
        this.password = password
    }

    @JsonIgnore
    override fun isAccountNonExpired() = true
    @JsonIgnore
    override fun isAccountNonLocked() = true
    @JsonIgnore
    override fun isCredentialsNonExpired() = true
    @JsonIgnore
    override fun isEnabled() = true

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
        if (role != other.role) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + age
        result = 31 * result + activity
        result = 31 * result + name.hashCode()
        result = 31 * result + gender.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + city.hashCode()
        result = 31 * result + purpose.hashCode()
        result = 31 * result + role.hashCode()
        return result
    }
}