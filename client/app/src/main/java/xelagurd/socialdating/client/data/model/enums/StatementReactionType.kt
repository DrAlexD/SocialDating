package xelagurd.socialdating.client.data.model.enums

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import xelagurd.socialdating.client.R

enum class StatementReactionType(
    @param:StringRes val descriptionRes: Int,
    @param:DrawableRes val iconRes: Int
) {
    FULL_NO_MAINTAIN(R.string.full_no_maintain, R.drawable.thumb_down),
    PART_NO_MAINTAIN(R.string.part_no_maintain, R.drawable.outline_thumb_down),
    NOT_SURE(R.string.not_sure, R.drawable.outline_thumbs_up_down),
    PART_MAINTAIN(R.string.part_maintain, R.drawable.outline_thumb_up),
    FULL_MAINTAIN(R.string.full_maintain, R.drawable.thumb_up)
}