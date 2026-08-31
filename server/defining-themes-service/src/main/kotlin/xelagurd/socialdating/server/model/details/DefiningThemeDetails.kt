package xelagurd.socialdating.server.model.details

import jakarta.validation.constraints.Positive
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_NAME_LENGTH_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_NAME_LENGTH_MIN
import xelagurd.socialdating.server.model.DefaultDataProperties.OPINION_LENGTH_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.OPINION_LENGTH_MIN
import xelagurd.socialdating.server.model.DefiningTheme
import xelagurd.socialdating.server.validation.TrimmedSize

data class DefiningThemeDetails(
    @field:TrimmedSize(min = DEFINING_THEME_NAME_LENGTH_MIN, max = DEFINING_THEME_NAME_LENGTH_MAX)
    val name: String,

    @field:TrimmedSize(min = OPINION_LENGTH_MIN, max = OPINION_LENGTH_MAX)
    val fromOpinion: String,

    @field:TrimmedSize(min = OPINION_LENGTH_MIN, max = OPINION_LENGTH_MAX)
    val toOpinion: String,

    @field:Positive
    val categoryId: Int
) {
    fun toDefiningTheme(numberInCategory: Int?) =
        DefiningTheme(
            name = name,
            fromOpinion = fromOpinion,
            toOpinion = toOpinion,
            categoryId = categoryId,
            numberInCategory = numberInCategory ?: 1
        )
}