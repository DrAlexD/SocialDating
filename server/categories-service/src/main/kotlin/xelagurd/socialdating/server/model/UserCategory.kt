package xelagurd.socialdating.server.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import xelagurd.socialdating.server.model.DefaultDataProperties.CATEGORY_INTEREST_INITIAL

@Entity(name = "user_categories")
@Table(
    name = "user_categories",
    indexes = [
        Index(columnList = "category_id, user_id", unique = true)
    ]
)
class UserCategory(
    @field:Id
    @field:GeneratedValue(GenerationType.IDENTITY)
    var id: Int? = null,

    @field:Column(columnDefinition = "integer check (interest between 0 and 100)")
    var interest: Int = CATEGORY_INTEREST_INITIAL,

    var userId: Int,

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
        var result = id ?: 0
        result = 31 * result + interest
        result = 31 * result + userId
        result = 31 * result + categoryId
        return result
    }
}