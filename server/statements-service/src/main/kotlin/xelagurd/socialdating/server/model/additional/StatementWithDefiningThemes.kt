package xelagurd.socialdating.server.model.additional

import xelagurd.socialdating.server.model.common.DefiningThemeReactionDetails

data class StatementWithDefiningThemes(
    val id: Int,
    val text: String,
    val creatorUserId: Int,
    val definingThemes: List<DefiningThemeReactionDetails>
)
