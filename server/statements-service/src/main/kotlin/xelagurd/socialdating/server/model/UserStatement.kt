package xelagurd.socialdating.server.model

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import xelagurd.socialdating.server.model.enums.StatementReactionType

@Entity(name = "user_statements")
@Table(
    name = "user_statements",
    indexes = [
        Index(columnList = "statement_id, user_id", unique = true)
    ]
)
class UserStatement(
    @field:Id
    @field:GeneratedValue(GenerationType.IDENTITY)
    var id: Int? = null,

    @field:Enumerated(EnumType.STRING)
    var reactionType: StatementReactionType,

    var userId: Int,

    var statementId: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserStatement

        if (id != other.id) return false
        if (reactionType != other.reactionType) return false
        if (userId != other.userId) return false
        if (statementId != other.statementId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + reactionType.hashCode()
        result = 31 * result + userId
        result = 31 * result + statementId
        return result
    }
}