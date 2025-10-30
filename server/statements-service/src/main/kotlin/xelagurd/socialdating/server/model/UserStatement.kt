package xelagurd.socialdating.server.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Min
import xelagurd.socialdating.server.model.enums.StatementReactionType

@Entity(name = "user_statements")
@Table(name = "user_statements")
class UserStatement(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int = 0,

    @field:Enumerated(EnumType.STRING)
    @field:Column(nullable = false)
    var reactionType: StatementReactionType,

    @field:Column(nullable = false)
    @field:Min(value = 1)
    var userId: Int,

    @field:Column(nullable = false)
    @field:Min(value = 1)
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
        var result = id
        result = 31 * result + reactionType.hashCode()
        result = 31 * result + userId
        result = 31 * result + statementId
        return result
    }
}