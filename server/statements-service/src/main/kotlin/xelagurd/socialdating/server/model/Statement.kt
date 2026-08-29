package xelagurd.socialdating.server.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import xelagurd.socialdating.server.model.additional.StatementWithDefiningThemes

@Entity(name = "statements")
@Table(name = "statements")
class Statement(
    @field:Id
    @field:GeneratedValue(GenerationType.IDENTITY)
    var id: Int? = null,

    @field:Column(unique = true)
    var text: String,

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
