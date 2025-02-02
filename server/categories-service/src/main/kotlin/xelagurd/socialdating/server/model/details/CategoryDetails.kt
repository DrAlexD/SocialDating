package xelagurd.socialdating.server.model.details

import xelagurd.socialdating.server.model.Category

data class CategoryDetails(
    val name: String
) {
    fun toCategory(): Category =
        Category(name = name)
}