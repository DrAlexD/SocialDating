package xelagurd.socialdating.server.model.common

import jakarta.validation.Valid
import jakarta.validation.constraints.Positive

data class CategoryUpdateDetails(
    @field:Positive
    val categoryId: Int,

    @field:Valid
    val maintainedListUpdates: List<MaintainedListUpdate> = listOf()
)
