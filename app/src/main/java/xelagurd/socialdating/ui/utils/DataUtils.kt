package xelagurd.socialdating.ui.utils

import xelagurd.socialdating.data.model.Category
import xelagurd.socialdating.ui.model.CategoryDetails

fun CategoryDetails.toCategory() = Category(
    id = id,
    name = name
)

fun Category.toCategoryDetails() = CategoryDetails(
    id = id,
    name = name
)
