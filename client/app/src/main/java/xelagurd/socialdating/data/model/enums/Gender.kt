package xelagurd.socialdating.data.model.enums

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import xelagurd.socialdating.R

enum class Gender(@StringRes val descriptionRes: Int, val color: Color) {
    MALE(R.string.male, Color(0xFFADD8E6)),
    FEMALE(R.string.female, Color(0xFFFFC1E3)),
}