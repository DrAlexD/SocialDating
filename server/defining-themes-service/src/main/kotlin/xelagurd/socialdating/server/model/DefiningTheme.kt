package xelagurd.socialdating.server.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

@Entity(name = "defining_themes")
@Table(name = "defining_themes")
class DefiningTheme(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int = 0,

    @field:Column(nullable = false, unique = true)
    @field:NotBlank
    var name: String,

    @field:Column(nullable = false)
    @field:NotBlank
    var fromOpinion: String,

    @field:Column(nullable = false)
    @field:NotBlank
    var toOpinion: String,

    @field:Column(nullable = false)
    @field:Min(value = 1)
    var categoryId: Int
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
        var result = id
        result = 31 * result + categoryId
        result = 31 * result + name.hashCode()
        result = 31 * result + fromOpinion.hashCode()
        result = 31 * result + toOpinion.hashCode()
        return result
    }
}