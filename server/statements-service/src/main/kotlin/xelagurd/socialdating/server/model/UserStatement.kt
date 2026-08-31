package xelagurd.socialdating.server.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import xelagurd.socialdating.server.model.DefaultDataProperties.ID_MIN
import xelagurd.socialdating.server.model.enums.StatementReactionType

@Entity(name = "user_statements")
@Table(
    name = "user_statements",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_statement_id__user_id", columnNames = ["statement_id", "user_id"])
    ]
)
class UserStatement(
    @field:Id
    @field:GeneratedValue(GenerationType.IDENTITY)
    var id: Int? = null,

    @field:Enumerated(EnumType.STRING)
    @field:Column(nullable = false)
    var reactionType: StatementReactionType,

    @field:Column(nullable = false, columnDefinition = "integer check (user_id >= $ID_MIN)")
    var userId: Int,

    @field:Column(nullable = false, columnDefinition = "integer check (statement_id >= $ID_MIN)")
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