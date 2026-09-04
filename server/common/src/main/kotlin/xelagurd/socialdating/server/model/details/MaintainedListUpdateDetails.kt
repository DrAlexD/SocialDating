package xelagurd.socialdating.server.model.details

import jakarta.validation.constraints.Positive
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType

data class MaintainedListUpdateDetails(
    val updateType: MaintainedListUpdateType,

    @field:Positive
    val numberInCategory: Int
)
