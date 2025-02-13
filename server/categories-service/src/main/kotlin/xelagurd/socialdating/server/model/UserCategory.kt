package xelagurd.socialdating.server.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Entity(name = "user_categories")
@Table(name = "user_categories")
class UserCategory(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int = 0,

    @field:Column(nullable = false)
    @field:Min(value = 0)
    @field:Max(value = 100)
    var interest: Int,

    @field:Column(nullable = false)
    @field:Min(value = 1)
    var userId: Int,

    @field:Column(nullable = false)
    @field:Min(value = 1)
    var categoryId: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserCategory

        if (id != other.id) return false
        if (interest != other.interest) return false
        if (userId != other.userId) return false
        if (categoryId != other.categoryId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + interest
        result = 31 * result + userId
        result = 31 * result + categoryId
        return result
    }
}