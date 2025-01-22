package xelagurd.socialdating.dto

data class CategoryDetails(
    val name: String
) {
    fun toCategory(): Category =
        Category(name = name)
}