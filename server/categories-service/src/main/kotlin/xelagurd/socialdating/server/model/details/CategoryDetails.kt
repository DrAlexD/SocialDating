package xelagurd.socialdating.server.model.details

import xelagurd.socialdating.server.model.Category
import xelagurd.socialdating.server.model.DefaultDataProperties.CATEGORY_NAME_LENGTH_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.CATEGORY_NAME_LENGTH_MIN
import xelagurd.socialdating.server.validation.TrimmedSize

data class CategoryDetails(
    @field:TrimmedSize(min = CATEGORY_NAME_LENGTH_MIN, max = CATEGORY_NAME_LENGTH_MAX)
    val name: String
) {
    fun toCategory() = Category(name = name)
}