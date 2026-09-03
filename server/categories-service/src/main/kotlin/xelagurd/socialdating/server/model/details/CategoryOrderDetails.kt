package xelagurd.socialdating.server.model.details

import jakarta.validation.constraints.Positive

data class CategoryOrderDetails(
    @field:Positive
    val categoryId: Int,

    @field:Positive
    val orderNumber: Int
)
