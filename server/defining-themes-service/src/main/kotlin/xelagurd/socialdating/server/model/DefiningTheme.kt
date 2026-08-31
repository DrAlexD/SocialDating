package xelagurd.socialdating.server.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_NAME_LENGTH_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_NAME_LENGTH_MIN
import xelagurd.socialdating.server.model.DefaultDataProperties.ID_MIN
import xelagurd.socialdating.server.model.DefaultDataProperties.OPINION_LENGTH_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.OPINION_LENGTH_MIN

@Entity(name = "defining_themes")
@Table(
    name = "defining_themes",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_name", columnNames = ["name"]),
        UniqueConstraint(
            name = "uk_category_id__number_in_category",
            columnNames = ["category_id", "number_in_category"]
        )
    ]
)
class DefiningTheme(
    @field:Id
    @field:GeneratedValue(GenerationType.IDENTITY)
    var id: Int? = null,

    @field:Column(
        nullable = false,
        columnDefinition = "varchar($DEFINING_THEME_NAME_LENGTH_MAX) " +
                "check (length(trim(name)) between $DEFINING_THEME_NAME_LENGTH_MIN " +
                "and $DEFINING_THEME_NAME_LENGTH_MAX)"
    )
    var name: String,

    @field:Column(
        nullable = false,
        columnDefinition = "varchar($OPINION_LENGTH_MAX) " +
                "check (length(trim(from_opinion)) between $OPINION_LENGTH_MIN and $OPINION_LENGTH_MAX)"
    )
    var fromOpinion: String,

    @field:Column(
        nullable = false,
        columnDefinition = "varchar($OPINION_LENGTH_MAX) " +
                "check (length(trim(to_opinion)) between $OPINION_LENGTH_MIN and $OPINION_LENGTH_MAX)"
    )
    var toOpinion: String,

    @field:Column(nullable = false, columnDefinition = "integer check (category_id >= $ID_MIN)")
    var categoryId: Int,

    @field:Column(nullable = false, columnDefinition = "integer check (number_in_category >= $ID_MIN)")
    var numberInCategory: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DefiningTheme

        if (id != other.id) return false
        if (categoryId != other.categoryId) return false
        if (numberInCategory != other.numberInCategory) return false
        if (name != other.name) return false
        if (fromOpinion != other.fromOpinion) return false
        if (toOpinion != other.toOpinion) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + categoryId
        result = 31 * result + numberInCategory
        result = 31 * result + name.hashCode()
        result = 31 * result + fromOpinion.hashCode()
        result = 31 * result + toOpinion.hashCode()
        return result
    }
}