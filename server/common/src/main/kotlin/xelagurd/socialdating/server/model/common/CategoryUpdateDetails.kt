package xelagurd.socialdating.server.model.common

data class CategoryUpdateDetails(
    val categoryId: Int,
    val maintainedListUpdates: List<MaintainedListUpdate> = listOf()
)
