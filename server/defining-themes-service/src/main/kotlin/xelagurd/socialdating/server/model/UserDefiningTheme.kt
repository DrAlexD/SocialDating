package xelagurd.socialdating.server.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Entity(name = "user_defining_themes")
@Table(name = "user_defining_themes")
class UserDefiningTheme(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,

    @field:Column(name = "udt_value", nullable = false)
    @field:Min(value = 0)
    @field:Max(value = 100)
    var value: Int,

    @field:Column(nullable = false)
    @field:Min(value = 0)
    @field:Max(value = 100)
    var interest: Int,


    var userCategoryId: Int,


    var definingThemeId: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserDefiningTheme

        if (id != other.id) return false
        if (value != other.value) return false
        if (interest != other.interest) return false
        if (userCategoryId != other.userCategoryId) return false
        if (definingThemeId != other.definingThemeId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + value
        result = 31 * result + interest
        result = 31 * result + userCategoryId
        result = 31 * result + definingThemeId
        return result
    }
}