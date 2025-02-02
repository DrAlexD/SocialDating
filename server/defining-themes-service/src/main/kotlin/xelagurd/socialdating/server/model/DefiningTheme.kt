package xelagurd.socialdating.server.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity(name = "defining_themes")
class DefiningTheme(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,
    var name: String? = null,
    var fromOpinion: String? = null,
    var toOpinion: String? = null,
    var categoryId: Int? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DefiningTheme

        if (id != other.id) return false
        if (categoryId != other.categoryId) return false
        if (name != other.name) return false
        if (fromOpinion != other.fromOpinion) return false
        if (toOpinion != other.toOpinion) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + (categoryId ?: 0)
        result = 31 * result + name.hashCode()
        result = 31 * result + fromOpinion.hashCode()
        result = 31 * result + toOpinion.hashCode()
        return result
    }
}