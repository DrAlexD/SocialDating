package xelagurd.socialdating.server.model.details

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import xelagurd.socialdating.server.model.UserCategory

data class UserCategoryDetails(
    @field:Min(value = 0)
    @field:Max(value = 100)
    var interest: Int,

    @field:Min(value = 1)
    var userId: Int,

    @field:Min(value = 1)
    var categoryId: Int
) {
    fun toUserCategory(): UserCategory =
        UserCategory(
            interest = interest,
            userId = userId,
            categoryId = categoryId
        )
}