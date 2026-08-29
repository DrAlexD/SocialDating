package xelagurd.socialdating.server.model.common

import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType

data class MaintainedListUpdate(
    val updateType: MaintainedListUpdateType,
    val numberInCategory: Int
)
