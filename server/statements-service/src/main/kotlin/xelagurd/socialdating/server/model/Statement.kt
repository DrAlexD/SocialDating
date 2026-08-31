package xelagurd.socialdating.server.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import xelagurd.socialdating.server.model.DefaultDataProperties.ID_MIN
import xelagurd.socialdating.server.model.DefaultDataProperties.STATEMENT_TEXT_LENGTH_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.STATEMENT_TEXT_LENGTH_MIN
import xelagurd.socialdating.server.model.additional.StatementWithDefiningThemes

@Entity(name = "statements")
@Table(
    name = "statements",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_text", columnNames = ["text"])
    ]
)
class Statement(
    @field:Id
    @field:GeneratedValue(GenerationType.IDENTITY)
    var id: Int? = null,

    @field:Column(
        nullable = false,
        columnDefinition = "varchar($STATEMENT_TEXT_LENGTH_MAX) " +
                "check (length(trim(text)) between $STATEMENT_TEXT_LENGTH_MIN and $STATEMENT_TEXT_LENGTH_MAX)"
    )
    var text: String,

    @field:Column(nullable = false, columnDefinition = "integer check (creator_user_id >= $ID_MIN)")
    var creatorUserId: Int
) {

    fun toStatementWithDefiningThemes(definingThemes: List<StatementDefiningTheme>) =
        StatementWithDefiningThemes(
            id = id!!,
            text = text,
            creatorUserId = creatorUserId,
            definingThemes = definingThemes.map { it.toDefiningThemeReactionDetails() }
        )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Statement

        if (id != other.id) return false
        if (creatorUserId != other.creatorUserId) return false
        if (text != other.text) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + creatorUserId
        result = 31 * result + text.hashCode()
        return result
    }
}
