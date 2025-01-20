package xelagurd.socialdating.dto

data class CategoryDetails(
    var name: String
) {
    fun toCategory(): Category =
        Category(name = name)
}