package xelagurd.socialdating.server.model

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.hibernate.annotations.Check
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
import jakarta.persistence.UniqueConstraint
import xelagurd.socialdating.server.model.DefaultDataProperties.AGE_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.AGE_MIN
import xelagurd.socialdating.server.model.DefaultDataProperties.CITY_LENGTH_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.CITY_LENGTH_MIN
import xelagurd.socialdating.server.model.DefaultDataProperties.EMAIL_LENGTH_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.EMAIL_LENGTH_MIN
import xelagurd.socialdating.server.model.DefaultDataProperties.EMAIL_PATTERN
import xelagurd.socialdating.server.model.DefaultDataProperties.NAME_LENGTH_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.NAME_LENGTH_MIN
import xelagurd.socialdating.server.model.DefaultDataProperties.PASSWORD_HASH_LENGTH
import xelagurd.socialdating.server.model.DefaultDataProperties.PERCENT_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.PERCENT_MIN
import xelagurd.socialdating.server.model.DefaultDataProperties.USERNAME_LENGTH_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.USERNAME_LENGTH_MIN
import xelagurd.socialdating.server.model.DefaultDataProperties.USERNAME_PATTERN
import xelagurd.socialdating.server.model.DefaultDataProperties.USER_ACTIVITY_INITIAL
import xelagurd.socialdating.server.model.additional.UserResponse
import xelagurd.socialdating.server.model.enums.AppLanguage
import xelagurd.socialdating.server.model.enums.Gender
import xelagurd.socialdating.server.model.enums.Purpose
import xelagurd.socialdating.server.model.enums.Role
import xelagurd.socialdating.server.utils.LocalizationUtils.localize

@Entity(name = "users")
@Table(
    name = "users",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_username", columnNames = ["username"]),
        UniqueConstraint(name = "uk_email", columnNames = ["email"])
    ]
)
@Check(name = "ck_name", constraints = "name_en is not null or name_ru is not null")
@Check(name = "ck_city", constraints = "city_en is not null or city_ru is not null")
class User(
    @field:Id
    @field:GeneratedValue(GenerationType.IDENTITY)
    var id: Int? = null,

    @field:Column(
        columnDefinition = "varchar($NAME_LENGTH_MAX) " +
                "check (length(trim(name_en)) between $NAME_LENGTH_MIN and $NAME_LENGTH_MAX)"
    )
    var nameEn: String? = null,

    @field:Column(
        columnDefinition = "varchar($NAME_LENGTH_MAX) " +
                "check (length(trim(name_ru)) between $NAME_LENGTH_MIN and $NAME_LENGTH_MAX)"
    )
    var nameRu: String? = null,

    @field:Enumerated(EnumType.STRING)
    @field:Column(nullable = false)
    var gender: Gender,

    @field:Column(
        nullable = false,
        columnDefinition = "varchar($USERNAME_LENGTH_MAX) " +
                "check (length(username) between $USERNAME_LENGTH_MIN and $USERNAME_LENGTH_MAX " +
                "and username ~ '$USERNAME_PATTERN')"
    )
    @JvmField
    final var username: String,

    @field:JsonIgnore
    @field:Column(
        nullable = false,
        columnDefinition = "varchar($PASSWORD_HASH_LENGTH) " +
                "check (length(password) = $PASSWORD_HASH_LENGTH)"
    )
    @JvmField
    final var password: String,

    @field:JsonIgnore
    @field:Column(
        columnDefinition = "varchar($EMAIL_LENGTH_MAX) " +
                "check (email is null or (length(trim(email)) between $EMAIL_LENGTH_MIN and $EMAIL_LENGTH_MAX " +
                "and email ~ '$EMAIL_PATTERN'))"
    )
    var email: String?,

    @field:Column(nullable = false, columnDefinition = "integer check (age between $AGE_MIN and $AGE_MAX)")
    var age: Int,

    @field:Column(
        columnDefinition = "varchar($CITY_LENGTH_MAX) " +
                "check (length(trim(city_en)) between $CITY_LENGTH_MIN and $CITY_LENGTH_MAX)"
    )
    var cityEn: String? = null,

    @field:Column(
        columnDefinition = "varchar($CITY_LENGTH_MAX) " +
                "check (length(trim(city_ru)) between $CITY_LENGTH_MIN and $CITY_LENGTH_MAX)"
    )
    var cityRu: String? = null,

    @field:Enumerated(EnumType.STRING)
    @field:Column(nullable = false)
    var purpose: Purpose,

    @field:Column(
        nullable = false,
        columnDefinition = "integer check (activity between $PERCENT_MIN and $PERCENT_MAX)"
    )
    var activity: Int = USER_ACTIVITY_INITIAL,

    @field:Enumerated(EnumType.STRING)
    @field:Column(nullable = false)
    val role: Role
) : UserDetails {

    fun toUserResponse(language: AppLanguage = AppLanguage.current()) =
        UserResponse(
            id = id!!,
            name = localize(nameEn, nameRu, language),
            gender = gender,
            username = username,
            age = age,
            city = localize(cityEn, cityRu, language),
            purpose = purpose,
            activity = activity,
            role = role
        )

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
        if (nameEn != other.nameEn) return false
        if (nameRu != other.nameRu) return false
        if (gender != other.gender) return false
        if (username != other.username) return false
        if (password != other.password) return false
        if (email != other.email) return false
        if (cityEn != other.cityEn) return false
        if (cityRu != other.cityRu) return false
        if (purpose != other.purpose) return false
        if (role != other.role) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + age
        result = 31 * result + activity
        result = 31 * result + nameEn.hashCode()
        result = 31 * result + nameRu.hashCode()
        result = 31 * result + gender.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + cityEn.hashCode()
        result = 31 * result + cityRu.hashCode()
        result = 31 * result + purpose.hashCode()
        result = 31 * result + role.hashCode()
        return result
    }
}
