package xelagurd.socialdating.server.model.details

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import xelagurd.socialdating.server.model.DefiningTheme

data class DefiningThemeDetails(
    @field:NotBlank
    @field:Size(max = 100)
    val name: String,

    @field:NotBlank
    @field:Size(max = 100)
    val fromOpinion: String,

    @field:NotBlank
    @field:Size(max = 100)
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