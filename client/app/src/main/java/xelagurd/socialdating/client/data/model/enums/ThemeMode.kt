package xelagurd.socialdating.client.data.model.enums

import androidx.annotation.StringRes
import xelagurd.socialdating.client.R

enum class ThemeMode(@param:StringRes override val descriptionRes: Int) : DescribedEnum {
    SYSTEM(R.string.theme_system),
    LIGHT(R.string.theme_light),
    DARK(R.string.theme_dark);

    companion object {
        fun fromName(name: String?) = entries.firstOrNull { it.name == name } ?: SYSTEM
    }
}
