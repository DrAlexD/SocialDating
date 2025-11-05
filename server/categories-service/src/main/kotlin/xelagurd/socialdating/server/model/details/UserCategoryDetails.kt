package xelagurd.socialdating.server.model.details

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import xelagurd.socialdating.server.model.DefaultDataProperties.PERCENT_MAX
import xelagurd.socialdating.server.model.UserCategory

data class UserCategoryDetails(
    @field:PositiveOrZero
    @field:Max(PERCENT_MAX.toLong())
    var interest: Int,

    @field:Positive
    var userId: Int,

    @field:Positive
    var categoryId: Int
) {
    fun toUserCategory() =
        UserCategory(
            interest = interest,
            userId = userId,
            categoryId = categoryId
        )
}