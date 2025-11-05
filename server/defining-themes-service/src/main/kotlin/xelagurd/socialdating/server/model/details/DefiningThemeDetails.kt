package xelagurd.socialdating.server.model.details

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import xelagurd.socialdating.server.model.DefaultDataProperties.LENGTH_MAX
import xelagurd.socialdating.server.model.DefiningTheme

data class DefiningThemeDetails(
    @field:NotBlank
    @field:Size(max = LENGTH_MAX)
    val name: String,

    @field:NotBlank
    @field:Size(max = LENGTH_MAX)
    val fromOpinion: String,

    @field:NotBlank
    @field:Size(max = LENGTH_MAX)
    val toOpinion: String,

    @field:Positive
    val categoryId: Int
) {
    fun toDefiningTheme() =
        DefiningTheme(
            name = name,
            fromOpinion = fromOpinion,
            toOpinion = toOpinion,
            categoryId = categoryId
        )
}