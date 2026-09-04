package xelagurd.socialdating.server.model.details

import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Positive

data class UserCategoriesUpdateDetails(
    @field:Positive
    val userId: Int,

    @field:NotEmpty
    @field:Valid
    val categories: List<CategoryUpdateDetails>
)
