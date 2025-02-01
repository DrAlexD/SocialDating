package xelagurd.socialdating.server.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity(name = "user_categories")
class UserCategory(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,
    var interest: Int,
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