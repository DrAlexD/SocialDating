package xelagurd.socialdating.server.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity(name = "statements")
class Statement(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,
    var text: String,
    var isSupportDefiningTheme: Boolean,
    var definingThemeId: Int,
    var creatorUserId: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Statement

        if (id != other.id) return false
        if (isSupportDefiningTheme != other.isSupportDefiningTheme) return false
        if (definingThemeId != other.definingThemeId) return false
        if (creatorUserId != other.creatorUserId) return false
        if (text != other.text) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + isSupportDefiningTheme.hashCode()
        result = 31 * result + definingThemeId
        result = 31 * result + creatorUserId
        result = 31 * result + text.hashCode()
        return result
    }
}