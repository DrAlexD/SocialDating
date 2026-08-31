package xelagurd.socialdating.client.data.model.additional

import kotlinx.serialization.Serializable
import xelagurd.socialdating.client.data.model.DefaultDataProperties.ID_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidId

@Serializable
data class DefiningThemeReactionDetails(
    val definingThemeId: Int,
    val isSupportDefiningTheme: Boolean
) {
    init {
        require(definingThemeId.isValidId()) { "DefiningThemeId must be at least $ID_MIN" }
    }
}
