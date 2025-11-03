package xelagurd.socialdating.server.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_INTEREST_STEP

@Entity(name = "user_defining_themes")
@Table(
    name = "user_defining_themes",
    indexes = [
        Index(columnList = "defining_theme_id, user_id", unique = true)
    ]
)
class UserDefiningTheme(
    @field:Id
    @field:GeneratedValue(GenerationType.IDENTITY)
    var id: Int? = null,

    @field:Column(name = "udt_value", columnDefinition = "integer check (udt_value between 0 and 100)")
    var value: Int,

    @field:Column(columnDefinition = "integer check (interest between 0 and 100)")
    var interest: Int = DEFINING_THEME_INTEREST_STEP,

    var userId: Int,

    var definingThemeId: Int
) {

    fun copy(
        id: Int? = null,
        value: Int? = null,
        interest: Int? = null,
        userId: Int? = null,
        definingThemeId: Int? = null
    ) =
        UserDefiningTheme(
            id = id ?: this.id,
            value = value ?: this.value,
            interest = interest ?: this.interest,
            userId = userId ?: this.userId,
            definingThemeId = definingThemeId ?: this.definingThemeId
        )

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
        var result = id ?: 0
        result = 31 * result + value
        result = 31 * result + interest
        result = 31 * result + userId
        result = 31 * result + definingThemeId
        return result
    }
}