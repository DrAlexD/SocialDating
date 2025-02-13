package xelagurd.socialdating.server.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

@Entity(name = "statements")
@Table(name = "statements")
class Statement(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int = 0,

    @field:Column(nullable = false, unique = true)
    @field:NotBlank
    var text: String,

    @field:Column(nullable = false)
    var isSupportDefiningTheme: Boolean,

    @field:Column(nullable = false)
    @field:Min(value = 1)
    var definingThemeId: Int,

    @field:Column(nullable = false)
    @field:Min(value = 1)
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
        var result = id
        result = 31 * result + isSupportDefiningTheme.hashCode()
        result = 31 * result + definingThemeId
        result = 31 * result + creatorUserId
        result = 31 * result + text.hashCode()
        return result
    }
}