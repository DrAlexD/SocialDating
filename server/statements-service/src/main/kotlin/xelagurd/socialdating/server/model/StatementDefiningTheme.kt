package xelagurd.socialdating.server.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import xelagurd.socialdating.server.model.DefaultDataProperties.ID_MIN
import xelagurd.socialdating.server.model.common.DefiningThemeReactionDetails

@Entity(name = "statement_defining_themes")
@Table(
    name = "statement_defining_themes",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_statement_id__defining_theme_id",
            columnNames = ["statement_id", "defining_theme_id"]
        )
    ]
)
class StatementDefiningTheme(
    @field:Id
    @field:GeneratedValue(GenerationType.IDENTITY)
    var id: Int? = null,

    @field:Column(nullable = false, columnDefinition = "integer check (statement_id >= $ID_MIN)")
    var statementId: Int,

    @field:Column(nullable = false, columnDefinition = "integer check (defining_theme_id >= $ID_MIN)")
    var definingThemeId: Int,

    @field:Column(nullable = false)
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
