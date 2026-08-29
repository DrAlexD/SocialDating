package xelagurd.socialdating.server.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import xelagurd.socialdating.server.model.common.DefiningThemeReactionDetails

@Entity(name = "statement_defining_themes")
@Table(
    name = "statement_defining_themes",
    indexes = [
        Index(columnList = "statement_id, defining_theme_id", unique = true)
    ]
)
class StatementDefiningTheme(
    @field:Id
    @field:GeneratedValue(GenerationType.IDENTITY)
    var id: Int? = null,

    var statementId: Int,

    var definingThemeId: Int,

    var isSupportDefiningTheme: Boolean
) {

    fun toDefiningThemeReactionDetails() =
        DefiningThemeReactionDetails(
            definingThemeId = definingThemeId,
            isSupportDefiningTheme = isSupportDefiningTheme
        )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StatementDefiningTheme

        if (id != other.id) return false
        if (statementId != other.statementId) return false
        if (definingThemeId != other.definingThemeId) return false
        if (isSupportDefiningTheme != other.isSupportDefiningTheme) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + statementId
        result = 31 * result + definingThemeId
        result = 31 * result + isSupportDefiningTheme.hashCode()
        return result
    }
}
