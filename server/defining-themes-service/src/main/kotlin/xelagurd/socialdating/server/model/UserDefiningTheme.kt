package xelagurd.socialdating.server.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Entity(name = "user_defining_themes")
@Table(
    name = "user_defining_themes",
    indexes = [
        Index(columnList = "defining_theme_id, user_id", unique = true)
    ]
)
class UserDefiningTheme(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int = 0,

    @field:Column(name = "udt_value", nullable = false)
    @field:Min(value = 0)
    @field:Max(value = 100)
    var value: Int,

    @field:Column(nullable = false)
    @field:Min(value = 0)
    @field:Max(value = 100)
    var interest: Int,

    @field:Column(nullable = false)
    @field:Min(value = 1)
    var userId: Int,

    @field:Column(nullable = false)
    @field:Min(value = 1)
    var definingThemeId: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserDefiningTheme

        if (id != other.id) return false
        if (value != other.value) return false
        if (interest != other.interest) return false
        if (userId != other.userId) return false
        if (definingThemeId != other.definingThemeId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + value
        result = 31 * result + interest
        result = 31 * result + userId
        result = 31 * result + definingThemeId
        return result
    }
}