package xelagurd.socialdating.client.data.model.enums

import androidx.annotation.StringRes

interface DescribedEnum {
    @get:StringRes
    val descriptionRes: Int
}
