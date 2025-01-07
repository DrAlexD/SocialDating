package xelagurd.socialdating.data.model.enums

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.ui.graphics.vector.ImageVector
import xelagurd.socialdating.R

enum class StatementReactionType(@StringRes val descriptionRes: Int, val imageVector: ImageVector) {
    FULL_NO_MAINTAIN(R.string.full_no_maintain, Icons.Default.ThumbUp),
    PART_NO_MAINTAIN(R.string.part_no_maintain, Icons.Outlined.ThumbUp),
    NOT_SURE(R.string.not_sure, Icons.Outlined.ThumbUp),
    PART_MAINTAIN(R.string.part_maintain, Icons.Outlined.ThumbUp),
    FULL_MAINTAIN(R.string.full_maintain, Icons.Default.ThumbUp)
}