package xelagurd.socialdating.client.data.model.enums

import androidx.annotation.StringRes
import xelagurd.socialdating.client.R

enum class DifferenceRangeType(@param:StringRes override val descriptionRes: Int) : DescribedEnum {
    DEFAULT(R.string.default_similarity),
    CUSTOM(R.string.custom_similarity)
}
