package xelagurd.socialdating.server.model.details

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import xelagurd.socialdating.server.model.DefiningTheme

data class DefiningThemeDetails(
    @field:NotBlank
    val name: String,

    @field:NotBlank
    val fromOpinion: String,

    @field:NotBlank
    val toOpinion: String,

    @field:Min(value = 1)
    val categoryId: Int
) {
    fun toDefiningTheme(): DefiningTheme =
        DefiningTheme(
            name = name,
            fromOpinion = fromOpinion,
            toOpinion = toOpinion,
            categoryId = categoryId
        )
}