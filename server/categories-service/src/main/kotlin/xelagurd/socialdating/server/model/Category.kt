package xelagurd.socialdating.server.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import xelagurd.socialdating.server.model.DefaultDataProperties.CATEGORY_NAME_LENGTH_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.CATEGORY_NAME_LENGTH_MIN

@Entity(name = "categories")
@Table(
    name = "categories",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_name", columnNames = ["name"])
    ]
)
class Category(
    @field:Id
    @field:GeneratedValue(GenerationType.IDENTITY)
    var id: Int? = null,

    @field:Column(
        nullable = false,
        columnDefinition = "varchar($CATEGORY_NAME_LENGTH_MAX) " +
                "check (length(trim(name)) between $CATEGORY_NAME_LENGTH_MIN and $CATEGORY_NAME_LENGTH_MAX)"
    )
    var name: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Category

        if (id != other.id) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + name.hashCode()
        return result
    }
}