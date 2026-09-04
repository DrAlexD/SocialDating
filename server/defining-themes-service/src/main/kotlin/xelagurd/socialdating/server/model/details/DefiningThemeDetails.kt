package xelagurd.socialdating.server.model.details

import jakarta.validation.constraints.Positive
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_NAME_LENGTH_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_NAME_LENGTH_MIN
import xelagurd.socialdating.server.model.DefaultDataProperties.ID_MIN
import xelagurd.socialdating.server.model.DefaultDataProperties.OPINION_LENGTH_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.OPINION_LENGTH_MIN
import xelagurd.socialdating.server.model.DefiningTheme
import xelagurd.socialdating.server.validation.TrimmedSize

data class DefiningThemeDetails(
    @field:TrimmedSize(min = DEFINING_THEME_NAME_LENGTH_MIN, max = DEFINING_THEME_NAME_LENGTH_MAX)
    val nameEn: String,

    @field:TrimmedSize(min = DEFINING_THEME_NAME_LENGTH_MIN, max = DEFINING_THEME_NAME_LENGTH_MAX)
    val nameRu: String,

    @field:TrimmedSize(min = OPINION_LENGTH_MIN, max = OPINION_LENGTH_MAX)
    val fromOpinionEn: String,

    @field:TrimmedSize(min = OPINION_LENGTH_MIN, max = OPINION_LENGTH_MAX)
    val fromOpinionRu: String,

    @field:TrimmedSize(min = OPINION_LENGTH_MIN, max = OPINION_LENGTH_MAX)
    val toOpinionEn: String,

    @field:TrimmedSize(min = OPINION_LENGTH_MIN, max = OPINION_LENGTH_MAX)
    val toOpinionRu: String,

    @field:Positive
    val categoryId: Int
) {
    fun toDefiningTheme(numberInCategory: Int?) =
        DefiningTheme(
            nameEn = nameEn,
            nameRu = nameRu,
            fromOpinionEn = fromOpinionEn,
            fromOpinionRu = fromOpinionRu,
            toOpinionEn = toOpinionEn,
            toOpinionRu = toOpinionRu,
            categoryId = categoryId,
            numberInCategory = numberInCategory ?: ID_MIN
        )
}
