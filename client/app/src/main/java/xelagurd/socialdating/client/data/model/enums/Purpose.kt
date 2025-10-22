package xelagurd.socialdating.client.data.model.enums

import androidx.annotation.StringRes
import xelagurd.socialdating.client.R

enum class Purpose(@param:StringRes val descriptionRes: Int) {
    FRIENDS(R.string.friends),
    RELATIONSHIPS(R.string.relationships),
    ALL_AT_ONCE(R.string.all_at_once)
}