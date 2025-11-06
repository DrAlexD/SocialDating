package xelagurd.socialdating.server.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity(name = "defining_themes")
@Table(
    name = "defining_themes",
    indexes = [
        Index(columnList = "category_id, number_in_category", unique = true)
    ]
)
class DefiningTheme(
    @field:Id
    @field:GeneratedValue(GenerationType.IDENTITY)
    var id: Int? = null,

    @field:Column(unique = true)
    var name: String,

    var fromOpinion: String,

    var toOpinion: String,

    var categoryId: Int,

    @field:JsonIgnore
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