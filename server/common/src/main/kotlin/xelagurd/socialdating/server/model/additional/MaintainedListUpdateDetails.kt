package xelagurd.socialdating.server.model.additional

import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType

data class MaintainedListUpdateDetails(
    val userId: Int,
    val categoryId: Int,
    val updateType: MaintainedListUpdateType,
    val numberInCategory: Int
)