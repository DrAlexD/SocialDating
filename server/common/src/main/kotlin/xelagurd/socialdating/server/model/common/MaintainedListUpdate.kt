package xelagurd.socialdating.server.model.common

import jakarta.validation.constraints.Positive
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType

data class MaintainedListUpdate(
    val updateType: MaintainedListUpdateType,

    @field:Positive
    val numberInCategory: Int
)
