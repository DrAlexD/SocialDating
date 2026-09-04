package xelagurd.socialdating.server.model.details

import jakarta.validation.Valid
import jakarta.validation.constraints.Positive

data class CategoryUpdateDetails(
    @field:Positive
    val categoryId: Int,

    @field:Valid
    val maintainedListUpdates: List<MaintainedListUpdateDetails> = listOf()
)
