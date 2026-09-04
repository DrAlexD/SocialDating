package xelagurd.socialdating.server.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import xelagurd.socialdating.server.model.DefaultDataProperties.CATEGORY_INTEREST_STEP
import xelagurd.socialdating.server.model.DefaultDataProperties.ID_MIN
import xelagurd.socialdating.server.model.DefaultDataProperties.PERCENT_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.PERCENT_MIN
import xelagurd.socialdating.server.model.dto.UserCategoryDto

@Entity(name = "user_categories")
@Table(
    name = "user_categories",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_category_id__user_id", columnNames = ["category_id", "user_id"])
    ]
)
class UserCategory(
    @field:Id
    @field:GeneratedValue(GenerationType.IDENTITY)
    var id: Int? = null,

    @field:Column(
        nullable = false,
        columnDefinition = "integer check (interest between $PERCENT_MIN and $PERCENT_MAX)"
    )
    var interest: Int = CATEGORY_INTEREST_STEP,

    @field:Column(nullable = false, columnDefinition = "integer check (user_id >= $ID_MIN)")
    var userId: Int,

    @field:Column(nullable = false, columnDefinition = "integer check (category_id >= $ID_MIN)")
    var categoryId: Int,

    @field:JdbcTypeCode(SqlTypes.ARRAY)
    @field:Column(
        columnDefinition = "bigint[] " +
                "check (maintained is null or array_position(maintained, null) is null)"
    )
    var maintained: Array<Long>? = null,

    @field:JdbcTypeCode(SqlTypes.ARRAY)
    @field:Column(
        columnDefinition = "bigint[] " +
                "check (not_maintained is null or array_position(not_maintained, null) is null)"
    )
    var notMaintained: Array<Long>? = null
) {

    fun toUserCategoryDto() =
        UserCategoryDto(
            id = id!!,
            interest = interest,
            userId = userId,
            categoryId = categoryId
        )

    fun copy(
        id: Int? = null,
        interest: Int? = null,
        userId: Int? = null,
        categoryId: Int? = null,
        maintained: Array<Long>? = null,
        notMaintained: Array<Long>? = null
    ) =
        UserCategory(
            id = id ?: this.id,
            interest = interest ?: this.interest,
            userId = userId ?: this.userId,
            categoryId = categoryId ?: this.categoryId,
            maintained = maintained ?: this.maintained,
            notMaintained = notMaintained ?: this.notMaintained
        )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserCategory

        if (id != other.id) return false
        if (interest != other.interest) return false
        if (userId != other.userId) return false
        if (categoryId != other.categoryId) return false
        if (!maintained.contentEquals(other.maintained)) return false
        if (!notMaintained.contentEquals(other.notMaintained)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + interest
        result = 31 * result + userId
        result = 31 * result + categoryId
        result = 31 * result + maintained.contentHashCode()
        result = 31 * result + notMaintained.contentHashCode()
        return result
    }
}